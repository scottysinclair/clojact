(ns ^:figwheel-hooks clojact.util
    (:require
     [re-frame.core :as rf]))

; utility functions for working with the data model

;true if the both maps have the same value for the given label
(defn matches [label mapA mapB]
  (= (label mapA) (label mapB)))



(defn update-booking
  [db booking prop-key prop-value]
  (let [index (.indexOf (get db :bookings) booking)]
     (assoc-in db  [:bookings index prop-key] prop-value)))



(defn get-category-by-id
  [db id]
  (some #(when (= id  (:id %)) %) 
        (get db :all-categories)))

(defn get-month-by-id
      [db id]
      ;      (prn "getm " id)
      (some #(when (= id  (:id %)) %)
            (get db :months)))

(defn decrement-current-month
      [db]
      (let [current-month-data (:current-month db)
            month (:month current-month-data)
            year (:year current-month-data)
            desired-month (if (= month 1) 12 (dec month))
            desired-year (if (= month 1) (dec year) year)
            ] (assoc db :current-month {:month desired-month :year desired-year})))

(defn increment-current-month
      [db]
      (let [current-month-data (:current-month db)
            month (:month current-month-data)
            year (:year current-month-data)
            desired-month (if (= month 12) 1 (inc month))
            desired-year (if (= month 12) (inc year) year)
            ] (assoc db :current-month {:month desired-month :year desired-year})))

;does the booking have the category?
(defn has-category[cat booking]
  (matches :id cat (:category booking)))

;filter all bookings with category
(defn bookings-with-category [cat bookings] 
  (filter (partial has-category cat) bookings))

;calculate total amount for the given category across the given bookings
(defn total-amount-for-category [cat bookings]
   (reduce + (map :amount (bookings-with-category cat bookings)) ))


;if a categoriy has value for a given booking
(defn category-has-value-for-bookings [bookings cat]
  (not= 0 (total-amount-for-category cat bookings)))

;filters on thge categories which have value for the given bookings
(defn categories-with-value [bookings categories]
  (let [has-value (partial category-has-value-for-bookings bookings)] 
   (filter has-value categories)))



;gets the next or previous category in the list depending on the inc-or-dec
(defn get-prev-or-next-cat [categories inc-or-dec cat]
  (println "GET " categories "   " cat)
  (nth categories (inc-or-dec (.indexOf categories cat))))


(let [select-n-o-p-category 
		  (fn [db element-id inc-or-dec];helper to select next or previous category
        (println "ELID" element-id)
		          (let [category-id (int (subs element-id 7))
		                cat (get-category-by-id db category-id)
                    bookings (:bookings db)
                    category-rows (categories-with-value bookings (:all-categories db))
		                next-cat (get-prev-or-next-cat category-rows inc-or-dec cat)] ;		                
		           (println "CHANGE SELCAT " cat next-cat) 
                  (rf/dispatch [:set-focus (js/document.getElementById (str "catrow-" (:id next-cat)))]) ;hack switch focus to next rows checkbox
		              (assoc db :selected-category next-cat)))
    ]
  
 ;sets selected-category to the category after the given element-id
(defn select-next-category
  [db element-id]
  (select-n-o-p-category db element-id inc))

 ;sets selected-category to the category before the given element-id
(defn select-prev-category
  [db element-id]
  (select-n-o-p-category db element-id dec))
)


(defn format-money [amount]
   (let [amount-str (str amount)
         len (count amount-str)]
     (cond (or (= amount 0) (= amount ""))
           amount-str
        :else
        (str (subs amount-str 0 (- len 2)) "," (subs amount-str (- len 2)))))) 


(ns ^:figwheel-hooks clojact.reframe
  (:require
   [goog.dom :as gdom]
   [clojact.material-core :as ui]
   [reagent.core :as reagent :refer [atom]]
   [re-frame.core :as rf]
   [day8.re-frame.http-fx :as rfx]
   [ajax.core :as ajax]
   [clojure.string :as str]
   [goog.string :as gstring]
   [goog.string.format]
   [clojact.util :as util]))



(rf/reg-event-fx                             ;; note the trailing -fx
  :initialize-load                      ;; usage:  (dispatch [:handler-with-http])
  (fn [{:keys [db]} _]                    ;; the first param will be "world"
    {:db   (assoc db :show-twirly true)   ;; causes the twirly-waiting-dialog to show??
     :http-xhrio {:method          :get
                  :uri             "/graph/exec"
                  :params          {:query (clojure.string/join [
                                                  "{" 
                                                  " categorys { id name }  "
                                                  " transactions: transactionsInMonth"
                                                  "    (fromDate:  \"01-10-2014\" toDate: \"01-11-2014\")" 
                                                  " {"
                                                  "    id date amount category { id name   } comment"
                                                  " }"
                                                  "}"])}
                  :timeout         8000                                           ;; optional see API docs
                  :response-format (ajax/json-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [:initialize-from-srv ]}}))
;                  :on-failure      [:bad-http-result]}}))


(rf/reg-event-db              ;; sets up initial application state
  :initialize-from-srv                 ;; usage:  (dispatch [:initialize])
  (fn [db [_ result]]                   ;; the two parameters are not important here, so use _
    {:time (js/Date.)         ;; What it returns becomes the new application state
     :time-color "#f88"
     :drawer-open false
     :selected-category nil
     :all-categories (:categorys result)
     :months (vector {:id 1 :name "January"}
                     {:id 2 :name "February"}
                     {:id 3 :name "March"}
                     {:id 4 :name "April"}
                     {:id 5 :name "May"}
                     {:id 6 :name "June"}
                     {:id 7 :name "July"}
                     {:id 8 :name "August"}
                     {:id 9 :name "September"}
                     {:id 10 :name "October"}
                     {:id 11 :name "November"}
                     {:id 12 :name "December"})
     :bookings (:transactions result)
     :current-month { :month 1 :year 2018 }
     }))


(rf/reg-event-db              ;; sets up initial application state
  :initialize-static                 ;; usage:  (dispatch [:initialize])
  (fn [db [_ result]]                   ;; the two parameters are not important here, so use _
    (prn result)
    {:time (js/Date.)         ;; What it returns becomes the new application state
     :time-color "#f88"
     :drawer-open false
     :selected-category nil
     :all-categories (vector 
                       {:id 1 :name "Food"} 
                       {:id 2 :name "Rent"} 
                       {:id 3 :name "Lunch"}
                       {:id 4 :name "Travel"}
                       {:id 5 :name "Car"}
                       {:id 6 :name "School"}
                       {:id 7 :name "Birthdays"}
                       {:id 8 :name "Christmas"}
                       {:id 9 :name "Camping"})
     :months (vector {:id 1 :name "January"}
                     {:id 2 :name "February"}
                     {:id 3 :name "March"}
                     {:id 4 :name "April"}
                     {:id 5 :name "May"}
                     {:id 6 :name "June"}
                     {:id 7 :name "July"}
                     {:id 8 :name "August"}
                     {:id 9 :name "September"}
                     {:id 10 :name "October"}
                     {:id 11 :name "November"}
                     {:id 12 :name "December"})
     :bookings (vector 
      {:id 1
       :date "01"
       :category {:id 1 :name "Food"}
       :comment "...."
       :amount 8221}
      {:id 2
       :date "02"
       :category {:id 2 :name "Rent"}
       :comment "...."
       :amount 82000
      }
      {:id 3
       :date "03"
       :category {:id 1 :name "Food"}
       :comment "...."
       :amount 8221}
      {:id 4
       :date "04"
       :category {:id 4 :name "Travel"}
       :comment "...."
       :amount 82000
      }
      {:id 5
       :date "05"
       :category {:id 1 :name "Food"}
       :comment "...."
       :amount 8221}
      {:id 6
       :date "06"
       :category {:id 2 :name "Rent"}
       :comment "...."
       :amount 82000
      }
      {:id 7
       :date "07"
       :category {:id 1 :name "Food"}
       :comment "...."
       :amount 8221}
      {:id 8
       :date "08"
       :category {:id 2 :name "Rent"}
       :comment "...."
       :amount 82000
      }
      {:id 9
       :date "09"
       :category {:id 1 :name "Food"}
       :comment "...."
       :amount 8221}
      {:id 10
       :date "10"
       :category {:id 2 :name "Rent"}
       :comment "...."
       :amount 82000
      }
      {:id 11
       :date "11"
       :category {:id 1 :name "Food"}
       :comment "...."
       :amount 8221}
      {:id 12
       :date "12"
       :category {:id 2 :name "Rent"}
       :comment "...."
       :amount 82000
      }
      {:id 13
       :date "13"
       :category {:id 1 :name "Food"}
       :comment "...."
       :amount 8221}
      {:id 14
       :date "14"
       :category {:id 2 :name "Rent"}
       :comment "...."
       :amount 82000
      }
      {:id 15
       :date "15"
       :category {:id 1 :name "Food"}
       :comment "...."
       :amount 8221}
      {:id 16
       :date "16"
       :category {:id 2 :name "Rent"}
       :comment "...."
       :amount 82000
      }
      )
     :current-month { :month 2 :year 2018 }
     }))



          ;; so the application state will initially be a map with two keys
(rf/reg-event-db                ;;the drawer open state is switched.
   :toggle-app-drawer
   (fn [db _]
     (assoc db :drawer-open (not (get db :drawer-open)))
   ))

(rf/reg-event-db
  :prev-month
  (fn [db _]
      (prn "prev month")
      (util/decrement-current-month db)))

(rf/reg-event-db
  :next-month
  (fn [db _]
      (prn "next month")
      (util/increment-current-month db)))

(rf/reg-event-db
    :booking-change
   (fn [db event]
     (let [booking (get event 1) ;the booking which is changing
           field   (get event 2) ; the booking field which is changing
           event-value (get event 3) ; the value from the ui compponent
           lookup-funct (or (get event 4) (fn [db value] value))] ;optional lookup conversion.
        (util/update-booking db booking field (lookup-funct db event-value)))))


(rf/reg-event-db
  :category-selected
  (fn [db event]
    (let [cat (get event 1)]
    (println "row selected " event)
    (assoc db :selected-category cat))))

(rf/reg-event-db
  :loaded-categories
  (fn [db categories]
    (assoc db :all-categories (vector 
                       {:id 1 :name "Food"} 
                       {:id 2 :name "Rent"} 
                       {:id 3 :name "Lunch"}
                       {:id 4 :name "Travel"}
                       {:id 5 :name "Car"}
                       {:id 6 :name "School"}
                       {:id 7 :name "Birthdays"}
                       {:id 8 :name "Christmas"}
                       {:id 9 :name "Camping"}))))

(rf/reg-event-db
  :set-focus
  (fn [db event]
    (let [dom-element (get event 1)]
      (.focus dom-element)
    db)))

(rf/reg-event-db
  :doc-key-press
  (fn [db event]
    (let [element-id (get event 1)
          key-code (get event 2)]
    (println "KEY PRESS" event element-id key-code) 
    (cond (= key-code 40)
	    (cond (= (subs element-id 0 7) "catrow-") 
           (util/select-next-category db element-id)
	          :else db)
     :else 
     (cond (= key-code 38)
	     (cond (= (subs element-id 0 7) "catrow-") 
            (util/select-prev-category db element-id)
	           :else db)
      :else db)))))


;; Query  -------------------------------------------------------
(rf/reg-sub
  :drawer-open
  (fn [db _]
    (:drawer-open db)))

(rf/reg-sub
  :bookings
  (fn [db _]
    (:bookings db)))

(rf/reg-sub
  :all-categories
  (fn [db _]
    (:all-categories db)))

(rf/reg-sub
  :selected-category
  (fn [db _]
    (:selected-category db)))

(rf/reg-sub
  :current-month
  (fn [db _]
 ;     (prn "CM"(:current-month db))
      (let [current-month (:month (:current-month db))
            month (util/get-month-by-id db current-month )]
           (prn "FOUND " month)
           {:text (str (:name month) " "(:year (:current-month db)))})))


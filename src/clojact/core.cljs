(ns ^:figwheel-hooks clojact.core
  (:require
   [goog.dom :as gdom]
   [clojact.material-core :as ui]
   [reagent.core :as reagent :refer [atom]]
   [re-frame.core :as rf]
   [clojure.string :as str]
   ))

;(println "IC" icons)
;(println icons)

;;material ui
(def el reagent/as-element)
;;(defn icon [nme] [ui/FontIcon {:className "material-icons"} nme])
(defn color [nme] (aget ui/colors nme))

;; create a new theme based on the dark theme from Material UI
(defonce theme-defaults {:theme (ui/createMuiTheme
                                (-> ui/lightBaseTheme
                                   (js->clj :keywordize-keys true)
                                   (update :palette merge {:primary1Color (color "red")
                                                          :primary2Color (color "green")})                                      clj->js))})


;true if the both maps have the same value for the given label
(defn matches [label mapA mapB]
  (= (label mapA) (label mapB)))

;updates a property of a booking
(defn updateBooking
  [db booking prop-key prop-value]
  (let [index (.indexOf (get db :bookings) booking)]
     (assoc-in db  [:bookings index prop-key] prop-value)))

(defn pap [x] (println x) x) 

(defn get-category-by-id
  [db id]
  (some #(when (= id (:id %)) %) 
        (get db :all-categories)))



;; -- Domino 2 - Event Handlers -----------------------------------------------
(rf/reg-event-db              ;; sets up initial application state
  :initialize                 ;; usage:  (dispatch [:initialize])
  (fn [_ _]                   ;; the two parameters are not important here, so use _
    {:time (js/Date.)         ;; What it returns becomes the new application state
     :time-color "#f88"
     :drawer-open false
     :selected-category nil
     :all-categories (vector {:id 1 :name "Food"} {:id 2 :name "Rent"} {:id 3 :name "Lunch"})
     :bookings (vector 
      {:id 1
       :date "01.01.2001"
       :category {:id 1 :name "Food"}
       :comment "...."
       :amount 82.21}
      {:id 2
       :date "02.01.2001"
       :category {:id 2 :name "Ren3"}
       :comment "...."
       :amount 820
      })}))

          ;; so the application state will initially be a map with two keys
(rf/reg-event-db                ;;the drawer open state is switched.
   :toggle-app-drawer
   (fn [db _]
     (assoc db :drawer-open (not (get db :drawer-open)))
   ))

(rf/reg-event-db
    :booking-change
   (fn [db event]
     (let [booking (get event 1) ;the booking which is changing
           field   (get event 2) ; the booking field which is changing
           event-value (get event 3) ; the value from the ui compponent
           lookup-funct (or (get event 4) (fn [db value] value))] ;optional lookup conversion.
        (updateBooking db booking field (lookup-funct db event-value)))))


(rf/reg-event-db
  :category-selected
  (fn [db event]
    (let [cat (get event 1)]
    (println "row selected " event)
    (assoc db :selected-category cat))))

;; -- Domino 4 - Query  -------------------------------------------------------
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

;; -- Domino 5 - View Functions ----------------------------------------------

(defn category-select-field
  [booking]
  (let [cat-list @(rf/subscribe [:all-categories])
        category (get booking :category)]
	  [ui/select { :value (:id category) 
                 :onChange #(rf/dispatch [:booking-change booking :category (-> % .-target .-value) get-category-by-id])  }
    (for [cat  cat-list]
	   [ui/menu-item { :key (:id cat)  :value (:id cat) }(:name cat)])]))


(defn booking-table-row
   [booking selected-category]
   (let [is-selected (matches :id (:category booking)  selected-category) ] 
     [ui/table-row {:key (:id booking) :className (when is-selected "highlighted-row")}
		    [ui/table-cell {:key "date"} [ui/text-field {:value (get booking :date) 
                                                   :onChange #(rf/dispatch [:booking-change booking :date (-> % .-target .-value)]) }]]
		    [ui/table-cell {:key "cat"} [category-select-field booking]]
		    [ui/table-cell {:key "com"} [ui/text-field {:value (get booking :comment)
                                    :onChange #(rf/dispatch [:booking-change booking :comment (-> % .-target .-value)]) }]]
		    [ui/table-cell {:key "amount"} [ui/text-field {:value (get booking :amount) 
                                       :onChange #(rf/dispatch [:booking-change booking :amount  (-> % .-target .-value)]) }]]
	    ]))

(defn booking-table
  []
  (let [bookings @(rf/subscribe [:bookings])
        selected-category @(rf/subscribe [:selected-category])]
	  [ui/paper {:key "booking-table"}
	  [ui/table
	   [ui/table-head
	   [ui/table-row
		      [ui/table-cell {:key "date"}"Date"]
		      [ui/table-cell {:key "cat"} "Category"]
		      [ui/table-cell {:key "com"} "Comment"]
		      [ui/table-cell {:key "amount"} "Amount"]
	    ]]
	    [ui/table-body
        (for [booking bookings]
            [booking-table-row booking selected-category]
	     )]
  ]]))


(defn category-table-row 
  [cat selected-category bookings]
 (let [is-selected (matches :id cat selected-category) ] 
  [ui/table-row {
              :key (:id cat) 
              :on-click #(rf/dispatch [:category-selected cat])
              :className (when is-selected "highlighted-row")}
      [ui/table-cell {:key "chk"}
       [ui/checkbox {:checked is-selected } ]]
      [ui/table-cell {:key "cat"} (:name cat)]
      [ui/table-cell {:key "tot"} (reduce + (map (fn[v] (js/parseFloat (:amount v))) bookings))]
  ]))


(defn category-table
  []
  (let [categories @(rf/subscribe [:all-categories])
        selected-category @(rf/subscribe [:selected-category])
        bookings @(rf/subscribe [:bookings])]
  [ui/paper {:key "cat-table" :class "category-table"}
  [ui/table
   [ui/table-head 
   [ui/table-row {:key (get :id cat)}
	     [ui/table-cell {:key "sel"}"Selected"]
	     [ui/table-cell {:key "cat"} "Category"]
	     [ui/table-cell {:key "tot"} "Total"]
   ]]
    [ui/table-body
        (for [cat categories] 
          (category-table-row cat selected-category bookings ))
	     ]
     ]]))

(defn booking-page
   []
   [ui/grid {:container true :spacing 0}
    [ui/grid {:item true :xs "auto"}
	   [booking-table]]
    [ui/grid {:item true :xs 3}
	   [category-table]]
   ])


(defn drawer
  []
  [ui/drawer {:open @(rf/subscribe [:drawer-open]) :docked true}
    [ui/list-box
      [ui/list-item {:leftIcon (el [:i.material-icons "Options"])
                     :on-click #(rf/dispatch [:toggle-app-drawer])
      }]
       [ui/divider]
       [ui/list-item {:on-click #(rf/dispatch [:toggle-app-drawer])} "Overview" ]
       [ui/list-item "Booking"]
       [ui/list-item "Reports"]
       [ui/list-item "Settings"]
    ]
   ])



(defn ui
  []
  [ui/mui-theme-provider theme-defaults
  [:div
    [ui/app-bar {:position "static"}
             [ui/toolbar
                 [ui/icon-button {:on-click #(rf/dispatch [:toggle-app-drawer])  }  
                  [ui/ic-menu {:aria-label "Menu"}]] ;;
                 [ui/typography {:variant "h6" :color "inherit"} "Accounting"]
               ]]
     [drawer]
     [booking-page]
   ]])


(println "This text is printed from src/clojact/core.cljs. Go ahead and edit it and see reloading in action.")

(defn get-app-element []
  (gdom/getElement "app"))



(defn mount [el]
  (rf/dispatch-sync [:initialize]) 
  (reagent/render-component [ui] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

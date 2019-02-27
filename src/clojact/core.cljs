(ns ^:figwheel-hooks clojact.core
  (:require
   [goog.dom :as gdom]
   [clojact.material-core :as ui]
   [reagent.core :as reagent :refer [atom]]
   [re-frame.core :as rf]
   [clojure.string :as str]
   [goog.string :as gstring]
   [goog.string.format]
   [clojact.reframe :as crf]
   [clojact.util :as util]
   ))

;;material ui
(def el reagent/as-element)
;;(defn icon [nme] [ui/FontIcon {:className "material-icons"} nme])
(defn color [nme] (aget ui/colors nme))

;; create a new theme based on the dark theme from Material UI
(defonce theme-defaults {:theme (ui/createMuiTheme
                                (-> ui/lightBaseTheme
                                   (js->clj :keywordize-keys true)
                                   (update :palette merge {:primary1Color (color "red")
                                                          :primary2Color (color "green")})
                                   clj->js))})

;;View Functions ----------------------------------------------


(defn time-period-control[]
  [ui/paper {:key "time-period-control" }
   [ui/button {:variant "contained"}"previous"]
   [:span " October 2018 <============> Noveber 2018 " ]
   [ui/button {:variant "contained"} "next"]
   ])


(defn category-select-field
  [booking]
  (let [cat-list @(rf/subscribe [:all-categories])
        category (get booking :category)]
	  [ui/select { :value (:id category) 
                 :onChange #(rf/dispatch [:booking-change booking :category (-> % .-target .-value) util/get-category-by-id])  }
    (for [cat  cat-list]
	   [ui/menu-item { :key (:id cat)  :value (:id cat) }(:name cat)])]))



(defn booking-table-row
   [booking selected-category]
   (let [is-selected (util/matches :id (:category booking)  selected-category) ] 
     [ui/table-row {:className (when is-selected "highlighted-row")}
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
            ^{:key (:id booking)} [booking-table-row booking selected-category]
	     )]
  ]]))



(defn category-table-row 
  [cat selected-category bookings]
 (let [is-selected (util/matches :id cat selected-category) ] 
  [ui/table-row {
              :key (:id cat) 
              :on-click (fn [event] (rf/dispatch [:category-selected cat]) ;dispatch state event
                          (.focus (js/document.getElementById (str "catrow-" (:id cat))))) ;pure dom side effect, set focus to the row's checkbox
                                              ;so that key events can be used
              :className (when is-selected "highlighted-row")}
      [ui/table-cell {:key "chk"}
       [ui/checkbox {:checked is-selected :id (str "catrow-" (:id cat))}]]
      [ui/table-cell {:key "cat"} (:name cat)]
      [ui/table-cell {:key "tot"} (util/format-money (util/total-amount-for-category cat bookings))]
  ]))


(defn category-table
  []
  (let [categories @(rf/subscribe [:all-categories])
        selected-category @(rf/subscribe [:selected-category])
        bookings @(rf/subscribe [:bookings])]
  [ui/paper {:key "cat-table" :class "category-table"}
  [ui/table
   [ui/table-head 
   [ui/table-row {:key (get :id cat) }
	     [ui/table-cell {:key "sel" } "Selected"]
	     [ui/table-cell {:key "cat" } "Category"]
	     [ui/table-cell {:key "tot" } "Total"]
   ]]
    [ui/table-body
        (for [cat (util/categories-with-value bookings categories)] 
          (category-table-row cat selected-category bookings ))
	     ]
     ]]))

(defn booking-page
   []
   [ui/grid {:container true :spacing 0}
    [ui/grid {:item true :xs "auto"}
     [time-period-control]
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
  [:div  {:onKeyDown #(rf/dispatch [:doc-key-press (-> % .-target.id) (-> % .-keyCode)])}
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
  (rf/dispatch-sync [:initialize-static]) 
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

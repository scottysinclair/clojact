(ns ^:figwheel-hooks clojact.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent-material-ui.core :as ui]
   [re-frame.core :as rf]
   [clojure.string :as str]
   [reagent-material-ui.core :as ui]
   ))


;;material ui
(def el reagent/as-element)
(defn icon [nme] [ui/FontIcon {:className "material-icons"} nme])
(defn color [nme] (aget ui/colors nme))

;; create a new theme based on the dark theme from Material UI
(defonce theme-defaults {:muiTheme (ui/getMuiTheme
                                    (-> ui/darkBaseTheme
                                        (js->clj :keywordize-keys true)
                                        (update :palette merge {:primary1Color (color "amber500")
                                                                :primary2Color (color "amber700")})
                                        clj->js))})


;; -- Domino 1 - Event Dispatch -----------------------------------------------
(defn dispatch-timer-event
  []
  (let [now (js/Date.)]
    (rf/dispatch [:timer now])))  ;; <-- dispatch used

;; Call the dispatching function every second.
;; `defonce` is like `def` but it ensures only one instance is ever
;; created in the face of figwheel hot-reloading of this file.
(defonce do-timer (js/setInterval dispatch-timer-event 1000))

;; -- Domino 2 - Event Handlers -----------------------------------------------
(rf/reg-event-db              ;; sets up initial application state
  :initialize                 ;; usage:  (dispatch [:initialize])
  (fn [_ _]                   ;; the two parameters are not important here, so use _
    {:time (js/Date.)         ;; What it returns becomes the new application state
     :time-color "#f88"
     :drawer-open false
     }))    ;; so the application state will initially be a map with two keys

(rf/reg-event-db                ;; usage:  (dispatch [:time-color-change 34562])
  :time-color-change            ;; dispatched when the user enters a new colour into the UI text field
  (fn [db [_ new-color-value]]  ;; -db event handlers given 2 parameters:  current application state and event (a vector)
    (assoc db :time-color new-color-value)))   ;; compute and return the new application state

(rf/reg-event-db                 ;; usage:  (dispatch [:timer a-js-Date])
  :timer                         ;; every second an event of this kind will be dispatched
  (fn [db [_ new-time]]          ;; note how the 2nd parameter is destructured to obtain the data value
    (assoc db :time new-time)))  ;; compute and return the new application state


(rf/reg-event-db                ;;the drawer open state is switched.
   :toggle-app-drawer
   (fn [db _]
     (assoc db :drawer-open (not (get db :drawer-open)))
   ))

;; -- Domino 4 - Query  -------------------------------------------------------
(rf/reg-sub
  :time
  (fn [db _]     ;; db is current app state. 2nd unused param is query vector
    (:time db))) ;; return a query computation over the application state

(rf/reg-sub
  :time-color
  (fn [db _]
    (:time-color db)))

(rf/reg-sub
  :drawer-open
  (fn [db _]
    (:drawer-open db)))

;; -- Domino 5 - View Functions ----------------------------------------------

(defn clock
  []
  [:div.example-clock
   {:style {:color @(rf/subscribe [:time-color])}}
   (-> @(rf/subscribe [:time])
       .toTimeString
       (str/split " ")
       first)])

(defn color-input
  []
  [:div.color-input
   "Time color: "
   [:input {:type "text"
            :value @(rf/subscribe [:time-color])
            :on-change #(rf/dispatch [:time-color-change (-> % .-target .-value)])}]])  ;; <---

(defn booking-table
  []
  [ui/Paper
  [:table.booking-table
   [:thead
     [:tr
	     [:td "Date"]
	     [:td "Category"]
	     [:td "Comment"]
	     [:td "Amount"]
     ]
    ]
    [:tbody
     [:tr
	     [:td "01.01.2011"]
	     [:td "Food"]
	     [:td "..."]
	     [:td "10.34"]
     ]
     ]]
  ])

(defn category-table
  []
  [:div.category-table "Categories"])

(defn navigation-panel
  []
  [:div.navigation-panel
   "January 2018"
   [ui/TextField {
                  :id "date" 
                  :label "Month" 
                  :type "date" 
                  :defaultValue "2018-01-01"}]
   ])

(defn booking-page
   []
   [:div.booking-page
   [navigation-panel]
   [booking-table]
   [category-table]
   ])

(defn drawer
  []
  [ui/Drawer {:open @(rf/subscribe [:drawer-open]) :docked true}
    [ui/List
      [ui/ListItem {:leftIcon (el [:i.material-icons "Options"])
                    :on-click #(rf/dispatch [:toggle-app-drawer])
      }]
       [ui/Divider]
       [ui/ListItem {:on-click #(rf/dispatch [:toggle-app-drawer])} "Overview" ]
       [ui/ListItem "Booking"]
       [ui/ListItem "Reports"]
       [ui/ListItem "Settings"]
    ]
   ])


(defn ui
  []
  [ui/MuiThemeProvider theme-defaults
   [:div
     [ui/AppBar {:title "Accounting" :onLeftIconButtonTouchTap #(rf/dispatch [:toggle-app-drawer])  } ]
     [drawer]
     [booking-page]
     ]
   ])


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

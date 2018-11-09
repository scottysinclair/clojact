(ns ^:figwheel-hooks clojact.core
  (:require
   [goog.dom :as gdom]
   [clojact.material :as ui]
   [clojact.material-icons :as icons]
   [reagent.core :as reagent :refer [atom]]
   [re-frame.core :as rf]
   [clojure.string :as str]
   ))



;;material ui
(def el reagent/as-element)
;;(defn icon [nme] [ui/FontIcon {:className "material-icons"} nme])
;;(defn color [nme] (aget ui/colors nme))

;; create a new theme based on the dark theme from Material UI
;;(defonce theme-defaults {:muiTheme (ui/createMuiTheme
;;                                    (-> ui/darkBaseTheme
 ;;                                       (js->clj :keywordize-keys true)
  ;;                                      (update :palette merge {:primary1Color (color "amber500")
   ;;                                                             :primary2Color (color "amber700")})
    ;;                                    clj->js))})


;; -- Domino 1 - Event Dispatch -----------------------------------------------
(defn dispatch-timer-event
  []
  (let [now (js/Date.)]
    (rf/dispatch [:timer now])))  ;; <-- dispatch used

;; Call the dispatching function every second.
;; `defonce` is like `def` but it ensures only one instance is ever
;; created in the face of figwheel hot-reloading of this file.
(defonce do-timer (js/setInterval dispatch-timer-event 1000))


;;
;;;helper methods to update the app-state
;;
;updates a property of a booking
(defn updateBooking
  [db booking prop-key prop-value]
  (let [index (.indexOf (get db :bookings) booking)]
     (assoc-in db  [:bookings index prop-key] prop-value)))



;; -- Domino 2 - Event Handlers -----------------------------------------------
(rf/reg-event-db              ;; sets up initial application state
  :initialize                 ;; usage:  (dispatch [:initialize])
  (fn [_ _]                   ;; the two parameters are not important here, so use _
    {:time (js/Date.)         ;; What it returns becomes the new application state
     :time-color "#f88"
     :drawer-open false
     :all-categories (vector {:id 1 :name "Food"} {:id 2 :name "Rent"} {:id 3 :name "Lunch"})
     :bookings (vector 
      {:id 1
       :date "01.01.2001"
       :category "Food"
       :comment "...."
       :amount "82.21"}
      {:id 2
       :date "02.01.2001"
       :category "Rent"
       :comment "...."
       :amount "820"
      })}))
          ;; so the application state will initially be a map with two keys

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

(rf/reg-event-db
    :booking-comment-change
   (fn [db event]
     (let [booking (get event 1)
           new-value (get event 2)]
        (updateBooking db booking :comment new-value))))

(rf/reg-event-db
    :booking-amount-change
   (fn [db event]
     (let [booking (get event 1)
           new-value (get event 2)]
        (updateBooking db booking :amount new-value))))

(rf/reg-event-db
    :booking-date-change
   (fn [db event]
     (let [booking (get event 1)
           new-value (get event 2)]
        (updateBooking db booking :date new-value))))

(rf/reg-event-db
    :booking-category-change
   (fn [db event]
     (let [booking (get event 1)
           new-value (get event 2)]
        (updateBooking db booking :category new-value))))


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

(rf/reg-sub
  :bookings
  (fn [db _]
    (:bookings db)))

(rf/reg-sub
  :all-categories
  (fn [db _]
    (:all-categories db)))

;; -- Domino 5 - View Functions ----------------------------------------------

(defn clock
  []
  [:div.example-clock
   {:style {:color @(rf/subscribe [:time-color])}}
   (-> @(rf/subscribe [:time])
       .toTimeString
       first)])

(defn color-input
  []
  [:div.color-input
   "Time color: "
   [:input {:type "text"
            :value @(rf/subscribe [:time-color])
            :on-change #(rf/dispatch [:time-color-change (-> % .-target .-value)])}]])  ;; <---


(defn ui
  []
  ;;[ui/mui-theme-provider  {:mui-theme (ui/get-mui-theme)}
   [:div
     [ui/app-bar {:title "Accounting" :onLeftIconButtonTouchTap #(rf/dispatch [:toggle-app-drawer])  } 
              [ui/toolbar
                 [ui/icon-button  [icons/menu-icon {:aria-label "Menu"}]] ;;
                 [ui/typography {:variant "h6" :color "inherit"} "Photos"]
               ]
    ;; [drawer]
   ;;  [booking-page]
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

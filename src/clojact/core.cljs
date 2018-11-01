(ns ^:figwheel-hooks clojact.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [clojact.components :as comps]
   [reagent-material-ui.core :as ui]))

;; some helpers
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



(println "This text is printed from src/clojact/core.cljs. Go ahead and edit it and see reloading in action.")

(defn get-app-element []
  (gdom/getElement "app"))


(defn react-app []

 (let [is-open? (atom false)
        close #(reset! is-open? false)])
   (fn []
     [:div
     [ui/AppBar {:title "Accounting" :onLeftIconButtonTouchTap (fn [] (println "tap")) } ]
      [ui/Drawer {:open true :docked true}
       [ui/List
         [ui/ListItem {:leftIcon (el [:i.material-icons "home"])
                       :on-click (fn []
                                   (println "click item"))}
          "Home"]]]
      ]
   )
)

(defn hello-world []
  [ui/MuiThemeProvider theme-defaults
   [:div
    [react-app]
   ]]
 )


(defn mount [el]
  (reagent/render-component [hello-world] el))

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

(ns ^:figwheel-hooks clojact.accounting.drawerview
    (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent-material-ui.core :as ui]
   ))

(def el reagent/as-element)

(defn close []
  (println "close")
 )

(defn drawer[drawer-model]
[ui/Drawer {:open (get drawer-model "is-open?") :docked true}
       [ui/List
         [ui/ListItem {:leftIcon (el [:i.material-icons "Options"])
                       :on-click (fn []
                                   (println "click item"))}
          ]
         [ui/Divider]
         [ui/ListItem {:on-click (fn [] (close))} "Overview" ]
         [ui/ListItem {:on-click (fn [] (close))} "Booking"]
         [ui/ListItem {:on-click (fn [] (close))} "Reports"]
         [ui/ListItem {:on-click (fn [] (close))} "Settings"]
         ]] 
)
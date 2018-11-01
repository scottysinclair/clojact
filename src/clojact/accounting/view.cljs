(ns ^:figwheel-hooks clojact.accounting.view
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent-material-ui.core :as ui]
   [clojact.accounting.model :as md]
   [clojact.accounting.drawerview :as dr]
   ))
  
(def el reagent/as-element)

(defn react-app []
 (let [app-state (reagent/atom (md/init-model))
       is-open? (reagent/atom true)
       ]
   (fn []
     (println "render!!!")
     [:div
     [ui/AppBar {:title "Accounting" :onLeftIconButtonTouchTap #(reset! is-open? true) } ]
     (dr/drawer (get @app-state "drawer")) 
     ]
   )
  )
)
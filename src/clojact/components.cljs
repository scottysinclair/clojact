(ns ^:figwheel-hooks clojact.components
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]))
  

(def click-count (reagent/atom 8))

(defn inc-by2 [val](+ val 2))

(defn counting-component []
  [:div
   "The atom " [:code "click-count"] " has value " @click-count "."
   [:input  {:type "button" :value "click-me" :on-click #(swap! click-count inc-by2)}]
  ])

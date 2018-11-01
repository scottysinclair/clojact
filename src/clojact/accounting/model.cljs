(ns ^:figwheel-hooks clojact.accounting.model
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   ))
  
(defn init-model[]
   {:drawer {
             :is-open?  false
             :close #(reset! is-open? false)
             }
    }
)


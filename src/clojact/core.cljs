(ns ^:figwheel-hooks clojact.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]))

(println "This text is printed from src/clojact/core.cljs. Go ahead and edit it and see reloading in action.")

(defn multiply [a b] (* a b))


;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello world!"}))

(defn get-app-element []
  (gdom/getElement "app"))

(defn nice-table [postfix]
  [:table {:style {:border "1px solid black"}}
   [:thead [:tr
     [:td "First Name " postfix]
     [:td "Last Name " postfix]
     [:td "Sex "postfix]
    ]]
   [:tbody
    [:tr
	    [:td "Scott"]
	    [:td "Sinclair"]
	    [:td "male"]
    ]
   ]
  ])

(def click-count (reagent/atom 8))

(defn inc-by2 [val](+ val 2))

(defn counting-component []
  [:div
   "The atom " [:code "click-count"] " has value " @click-count "."
   [:input  {:type "button" :value "click-me" :on-click #(swap! click-count inc-by2)}]
  ])

(defn hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this in src/clojact/core.cljs and watch it change alot!"]
   [:p "This is a simple paragraph with some " [:b "Bold text!"]]
   [nice-table "!"]
   [counting-component]
   ])


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

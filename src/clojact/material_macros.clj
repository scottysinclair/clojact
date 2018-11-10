; shamelessly stolen from https://github.com/tuhlmann/reagent-material

(ns  ^:figwheel-hooks clojact.material-macros

(def material-tags
  '[AppBar])

(defn material-ui-react-import [tname]
  `(def ~tname)
      (cljs.util/debug-prn "==" (aget js/MaterialUI ~(name tname)))
     (reagent.core/adapt-react-class (aget js/MaterialUI ~(name tname))))



(defmacro export-material-ui-react-classes []
  `(do
     ~@(map material-ui-react-import material-tags)
     ))
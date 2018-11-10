(ns  ^:figwheel-hooks clojact.material-core
  (:refer-clojure :exclude [List])
  (:require [cljsjs/material-ui]
            [material-ui-icons :as icons] ;cljsjs
            [reagent.core :as r])
  )

;;(export-material-ui-react-classes)

;(println icons)

(def app-bar (r/adapt-react-class (aget js/MaterialUI "AppBar")))
(def toolbar (r/adapt-react-class (aget js/MaterialUI "Toolbar")))
(def typography (r/adapt-react-class (aget js/MaterialUI "Typography")))
(def icon-button (r/adapt-react-class (aget js/MaterialUI "IconButton")))
(def drawer (r/adapt-react-class (aget js/MaterialUI "Drawer")))
(def list-box (r/adapt-react-class (aget js/MaterialUI "List")))
(def list-item (r/adapt-react-class (aget js/MaterialUI "ListItem")))
(def divider (r/adapt-react-class (aget js/MaterialUI "Divider")))
(def paper (r/adapt-react-class (aget js/MaterialUI "Paper")))
(def table (r/adapt-react-class (aget js/MaterialUI "Table")))
(def table-head (r/adapt-react-class (aget js/MaterialUI "TableHead")))
(def table-body (r/adapt-react-class (aget js/MaterialUI "TableBody")))
(def table-row (r/adapt-react-class (aget js/MaterialUI "TableRow")))
(def table-cell (r/adapt-react-class (aget js/MaterialUI "TableCell")))
(def text-field (r/adapt-react-class (aget js/MaterialUI "TextField")))
(def select (r/adapt-react-class (aget js/MaterialUI "Select")))
(def menu-item (r/adapt-react-class (aget js/MaterialUI "MenuItem")))


(def ic-menu (r/adapt-react-class (aget js/MaterialUIIcons "Menu")))



(def colors (-> js/MaterialUI
                (aget "colors")))

(def lightBaseTheme (-> js/MaterialUIStyles
                        (aget "lightBaseTheme")))

(def darkBaseTheme (-> js/MaterialUIStyles
                       (aget "darkBaseTheme")))

(def getMuiTheme (-> js/MaterialUIStyles
                     (aget "getMuiTheme")))

(def createMuiTheme (-> js/MaterialUIStyles
                        (aget "createMuiTheme")))

(def mui-theme-provider (-> js/MaterialUIStyles
                            (aget "MuiThemeProvider")
                            (reagent.core/adapt-react-class)))


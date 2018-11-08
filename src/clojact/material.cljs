(ns  ^:figwheel-hooks clojact.material
  (:require [material-ui]
            [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as csx]
            [material-ui-icons]))

(def mui-theme-provider (aget js/MaterialUI "MuiThemeProvider"))

(defn create-mui-theme
  ([] (create-mui-theme nil))
  ([raw-theme]
   (->> raw-theme
        (csx/transform-keys csk/->camelCase)
        clj->js
        js/MaterialUI.createMuiTheme)))

(defn color
  ""
  [key shade]
  (get (js->clj (aget js/MaterialUI "colors" (name (csk/->camelCase key))))
       (name shade)))

;; components
(def button (aget js/MaterialUI "Button"))
(def app-bar (aget js/MaterialUI "AppBar"))
(def tool-bar (aget js/MaterialUI "Toolbar"))
(def paper (aget js/MaterialUI "Paper"))
(def grid (aget js/MaterialUI "Grid"))
(def icon-button (aget js/MaterialUI "IconButton"))
(def typography (aget js/MaterialUI "Typography"))
(def snack-bar (aget js/MaterialUI "Snackbar"))
(def drawer (aget js/MaterialUI "Drawer"))
(def list-box(aget js/MaterialUI "List"))
(def list-item(aget js/MaterialUI "ListItem"))
(def list-item-text(aget js/MaterialUI "ListItemText"))
(def list-item-icon(aget js/MaterialUI "ListItemIcon"))
(def divider(aget js/MaterialUI "Divider"))
(def avatar(aget js/MaterialUI "Avatar"))
(def table (aget js/MaterialUI "Table"))
(def table-header (aget js/MaterialUI "TableHeader"))
(def table-header-column (aget js/MaterialUI "TableHeaderColumn"))
(def table-body (aget js/MaterialUI "TableBody"))
(def table-row (aget js/MaterialUI "TableRow"))
(def table-row-column (aget js/MaterialUI "TableRowColumn"))
(def text-field (aget js/MaterialUI "TextField"))
(def paper (aget js/MaterialUI "Paper"))



;; icons
(def menu-icon (aget js/MaterialUIIcons "Menu"))
(def inbox-icon (aget js/MaterialUIIcons "Inbox"))
(def person-icon(aget js/MaterialUIIcons "Person"))
(def comunity-icon(aget js/MaterialUIIcons "People"))
(def assessment-icon(aget js/MaterialUIIcons "Assessment"))
(def statistics-icon(aget js/MaterialUIIcons "Description"))
(def help-icon(aget js/MaterialUIIcons "Help"))

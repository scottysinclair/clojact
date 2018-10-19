// Compiled by ClojureScript 1.10.339 {}
goog.provide('clojact.core');
goog.require('cljs.core');
goog.require('goog.dom');
goog.require('reagent.core');
cljs.core.println.call(null,"This text is printed from src/clojact/core.cljs. Go ahead and edit it and see reloading in action.");
clojact.core.multiply = (function clojact$core$multiply(a,b){
return (a * b);
});
if((typeof clojact !== 'undefined') && (typeof clojact.core !== 'undefined') && (typeof clojact.core.app_state !== 'undefined')){
} else {
clojact.core.app_state = reagent.core.atom.call(null,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"text","text",-1790561697),"Hello world!"], null));
}
clojact.core.get_app_element = (function clojact$core$get_app_element(){
return goog.dom.getElement("app");
});
clojact.core.hello_world = (function clojact$core$hello_world(){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h1","h1",-1896887462),new cljs.core.Keyword(null,"text","text",-1790561697).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,clojact.core.app_state))], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),"Edit this in src/clojact/core.cljs and watch it change!"], null)], null);
});
clojact.core.mount = (function clojact$core$mount(el){
return reagent.core.render_component.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [clojact.core.hello_world], null),el);
});
clojact.core.mount_app_element = (function clojact$core$mount_app_element(){
var temp__5457__auto__ = clojact.core.get_app_element.call(null);
if(cljs.core.truth_(temp__5457__auto__)){
var el = temp__5457__auto__;
return clojact.core.mount.call(null,el);
} else {
return null;
}
});
clojact.core.mount_app_element.call(null);
clojact.core.on_reload = (function clojact$core$on_reload(){
return clojact.core.mount_app_element.call(null);
});

//# sourceMappingURL=core.js.map

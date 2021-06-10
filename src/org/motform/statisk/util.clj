(ns org.motform.statisk.util
  "Utility functions."
  (:require [clojure.string :as str]))

;;;
;;; Convenience functions
;;;

(defmacro when-or
  "Evaluates body if (`pred` `x`) is true, otherwise return `x`."
  [pred x & body]
  (list 'if `(~pred ~x) (cons 'do body) x))

;;;
;;; User functions
;;;

(defn- final-ns
  "Return final part of ns, i.e. 'foo.bar.baz returns baz."
  [s]
  (-> s str (str/split #"\.") last))

(defn- map-kv
  "Return `m` with `kf` and `vf` applied to all surface level pairs."
  [m kf vf]
  (reduce-kv (fn [m k v] (assoc m (kf k) (vf v))) {} m))

(defn qualified-keyed-ns-publics
  "Return map of var-got vars `ns`, with the var names qualified to
  the final ns as keys."
  [ns]
  (map-kv (ns-publics ns)
          #(keyword (-> ns ns-name final-ns) (str %))
          var-get))

(defn pages-in-group
  "Return the subset of `pages` that are members of `group`."
  [group pages]
  (filter #(when-let [groups (:page/groups %)]
             (groups group))
          pages))

(comment 
  (qualified-keyed-ns-publics 'motform.portfolio.template)
  )

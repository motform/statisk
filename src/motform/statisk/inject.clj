(ns motform.statisk.inject
  "Injection functions for use under `:site/inject`. These are
  injected during pull and transform phases (should maybe be
  conditional?) and are expected to be side effecting only, i.e. not
  returning the site map (that is handle led by `core/build-site!`)."
  (:require [clojure.data :as data]))

(defn inject-site-fn
  "Injects `f` and calls it with `site` map."
  [f]
  (fn [site] (f site)))

;; TODO diff
;; to diff we need the site map before and after fn application
;; that would mean that we either use a fn that closes over the 
;; the orig map, calls fn, diffs and then returns
;; or something stateful that keep track
(defn- inject-data-diff
  "Injects a simple differ using `clojure.data/diff` that
   sends the diff to printfn."
  [printfn]
  (fn [site]
    (let [[old new] (data/diff site)])
    ))

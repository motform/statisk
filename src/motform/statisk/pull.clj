(ns motform.statisk.pull
  "Pulling functions for use under `:site/pull` during the first phase
  of`core/build-site!`. Pulling is executed sequentially and should
  return the site map."
  (:require [motform.statisk.fs :as fs]))

(defn- read-resource
  [{:resource/keys [file dir slug] :as resource}]
  (cond
    file (assoc resource :resource/renderer #(slurp file))
    dir  (assoc resource :resource/renderer #(fs/copy-dir! dir slug))
    :else resource))

(defn read-resources
  "Assoc a prepared renderer with contents of `:resource/file` to
  `:resouce/renderer`, if present."
  [{:site/keys [resources] :as site}]
  (assoc site :site/resources (map read-resource resources)))

(defn read-svgs
  "Assocs `:site/svgs` into `site` map,
   a map of svg-path to inline svg (slurped to a string)."
  [{:site/keys [svgs] :as site}]
  (assoc site :site/svgs
         (zipmap (map fs/file-name svgs)
                 (map #(slurp %) svgs))))

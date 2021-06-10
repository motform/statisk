(ns org.motform.statisk.core
  "Statisk is a very small static site generator that I wrote to make
  non-blog-like sites (portfolios). While primarily a fun/learning
  project, experience has shown me that the `.md` way of SSG proves a
  very crude fit to the type of pages that portfolios are comprised
  of. Instead, I'm trying out something `edn` based that might
  alleviate some of my precised frictions. 

  The architecture aims to be data driven and is heavily inspired by
  Reitit. It is important to note that every rebuild is done in full,
  no caching or other incremental shenanigans. This is by design –
  spitting some web pages is insanely trivial. Any type of build
  complexion is bound to fail and get tripped up somewhere (esp. in my
  experience of other SSGs).

  `org.motform.statisk.core` provides the primary API for re-building pages
  in a REPL environment. You could of course call it on the command
  line via clj -X, but that is not optimal unless you are doing a
  single build."
  (:require [clojure.main           :refer [demunge]]
            [clojure.string         :as str]
            [org.motform.statisk.fs :as fs]))

(defn- comp-seq
  "Takes seq `v` of functions and returns the ordered composition."
  [v]
  (->> v reverse (apply comp)))

(defn fname
  "the fact that 'munge' is an ugly word is intentional
  — noisesmith, clojurians 210118 "
  [f]
  (-> f class str demunge (str/split #" ") last))

(defn- inject [injection f]
  (fn [site]
    (println "---" (fname f) "---")
    (-> site f injection)))

(defn- prep-injection
  "Wrap injection function to return the `site` map after calling it."
  [injection]
  (fn [site]
    ((apply juxt injection) site)
    site))

(defn- comp-injection [injection fs]
  (if (empty? injection)
    (comp-seq fs)
    (->> fs
         (map (partial inject (prep-injection injection)))
         comp-seq)))

(defn build-site!
  "Build `site` in `:site/path` or in optional secondary `path` arg.
  Return the resulting file structure as an `fs/tree`."
  ([{:site/keys [inject pull transform push path] :as site}]
   (fs/empty-dir! path)
   (-> site
       ((comp-injection inject pull))
       ((comp-injection inject transform))
       ((apply juxt push)))
   (fs/tree path))
  ([site path]
   (build-site! (assoc site :site/path path))))

(comment
  (fs/tree "target")
  )



(ns motform.statisk.transform
  "Transforming functions for use under `:site/transform` during the
  first phase of`core/build-site!`. Transformation is executed sequentially
  and should return the site map.")

(defn- render-page
  "Render `page` with `page-rendrer` from `site` and assoc `:page/html` in page map."
  [{:site/keys [title-fn page-renderer templates] :as site}
   {:page/keys [template data] :as page}]
  (when template
    (assoc page :page/html (page-renderer (title-fn (:data/title data))
                                          site ; TODO decide what goes where
                                          ((template templates) data site)))))

(defn render-pages
  "Assoc function of `:site/page-renderer` to all `pages` of `site`.
   A page-renderer is a function of two arguments - `page` and `site`."
  [{:site/keys [pages] :as site}]
  (assoc site :site/pages (map #(render-page site %) pages)))

(defn- render-resource
  "Render `resource` with individual `rendrer`
   and assoc `:resource/concent` in resource map."
  [{:resource/keys [renderer] :as resource}]
  (if renderer
    (assoc resource :resource/content (renderer))
    resource))

(defn render-resources
  "Assoc function of `:resource/renderer` to applicable `resouces` to `site`."
  [{:site/keys [resources] :as site}]
  (assoc site :site/resources (map #(render-resource %) resources)))

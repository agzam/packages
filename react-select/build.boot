(set-env!
 :resource-paths #{"resources"}
 :dependencies '[[cljsjs/boot-cljsjs "0.10.4"  :scope "test"]
                 [cljsjs/react "16.8.6-0"]
                 [cljsjs/react-dom "16.8.6-0"]
                 ;; [cljsjs/prop-types "15.6.2-0"]
                 ;; [cljsjs/classnames "2.2.5-1"]
                 ;; [cljsjs/react-input-autosize "2.2.1-1"]
                 ;; [cljsjs/emotion "10.0.6-0"]
                 ])

(require '[cljsjs.boot-cljsjs.packaging :refer :all])

(def +lib-version+ "3.0.8")
(def +version+ (str +lib-version+ "-0"))

(task-options!
 pom  {:project     'cljsjs/react-select
       :version     +version+
       :description "A flexible and beautiful Select Input control for ReactJS with multiselect, autocomplete and ajax support."
       :url         "http://jedwatson.github.io/react-select/"
       :scm         {:url "https://github.com/cljsjs/packages"}
       :license     {"MIT" "http://opensource.org/licenses/MIT"}})

(deftask package []
  (comp
   (run-commands :commands [["npm" "install" "--include-dev"]
                            ["npm" "run" "build:dev"]
                            ["npm" "run" "build:prod"]
                            #_["rm" "-rf" "./node_modules"]])
   (sift :move {#".*react-select.inc.js" "cljsjs/react-select/development/react-select.inc.js"
                #".*react-select.min.inc.js" "cljsjs/react-select/production/react-select.min.inc.js"})
   (sift :include #{#"^cljsjs"})
   (deps-cljs :foreign-libs [{:file           #"react-select.inc.js"
                              :file-min       #"react-select.min.inc.js"
                              :provides       ["cljsjs.react-select"]
                              :global-exports '{"react-select" Select}
                              :requires       ["react" "react-dom"]}]
              :externs [#"react-select.ext.js"])
   (pom)
   (jar)
   (validate-checksums)))

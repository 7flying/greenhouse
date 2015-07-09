(defproject green-rules "0.1.0-SNAPSHOT"
  :description "Rules to keep plants and veggies alive"
  :url "https://github.com/7flying/greenhouse/tree/master/green-rules"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.toomuchcode/clara-rules "0.8.8"]]
  :main ^:skip-aot green-rules.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})

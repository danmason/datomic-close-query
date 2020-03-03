(ns query-close
  (:require [datomic.api :as d]))

(def movie-schema
  [{:db/ident :movie/title
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The title of the movie"}

   {:db/ident :movie/genre
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The genre of the movie"}

   {:db/ident :movie/release-year
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc "The year the movie was released in theaters"}])

(def first-movies
  [{:movie/title "The Goonies"
    :movie/genre "action/adventure"
    :movie/release-year 1985}
   {:movie/title "Commando"
    :movie/genre "action/adventure"
    :movie/release-year 1985}
   {:movie/title "Repo Man"
    :movie/genre "punk dystopia"
    :movie/release-year 1984}])

(defn -main []
  (let [uri (str "datomic:mem://test")]
    (try
      (d/delete-database uri)
      (d/create-database uri)
      (let [conn (d/connect uri)]
        (d/transact conn movie-schema)
        (d/transact conn first-movies)
        (let [db (d/db conn)]
          (d/query {:query '{:find [?e]
                             :where [[?e :movie/title]]}
                    :timeout 1
                    :args [db]}))
        (d/release conn))
      (finally
        (d/delete-database uri)))))

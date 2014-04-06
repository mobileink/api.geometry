(ns org.mobileink.api.geometry.igplementations
  (:use [org.mobileink.api.geometry.utils])
  (:require [org.mobileink.api.geometry.protocols :as gp]))

;; =====================================================
;; Implementation utilities
;;
;; Tools to support the registration / manangement of org.mobileink.api.geometry implementations

;; map of known implementation tags to namespace imports
;; we use this to attempt to load an implementation
(def KNOWN-IMPLEMENTATIONS
  (array-map
   ;; computational geometry
   :jts :TODO
   :javageom :TODO
   :geolib :TODO
   ;; graphics kits
   :awt 'org.mobileink.api.geometry.impl.awt
   :swing :TODO
   :swt :TODO  ;; Standard Widget Toolkit
   :sdl :TODO
   :processing :TODO
   :piccolo :TODO
   :commons-math 'apache-commons-matrix.core))

;; default implementation to use
;; should be included with org.mobileink.api.geometry for easy of use
(def DEFAULT-IMPLEMENTATION :awt)

;; hashmap of implementation keys to canonical objects
;; objects must implement PImplementation protocol at a minimum
(defonce canonical-objects (atom {}))

(defn get-implementation-key
  "Returns the implementation code for a given object"
  ([m]
    (cond 
      (keyword? m) m
      (gp/is-scalar? m) nil
      :else (gp/implementation-key m))))

(defn register-implementation
  "Registers a matrix implementation for use. Should be called by all implementations
   when they are loaded."
  ([canonical-object]
    (swap! canonical-objects assoc (gp/implementation-key canonical-object) canonical-object)))

(defn try-load-implementation
  "Attempts to load an implementation for the given keyword.
   Returns nil if not possible, a non-nil value otherwise."
  ([k]
    (if-let [ns-sym (KNOWN-IMPLEMENTATIONS k)]
      (try
        (do 
          (require ns-sym) 
          (if (@canonical-objects k) :ok :warning-implementation-not-registered?))
        (catch Throwable t nil)))))

(defn get-canonical-object
  "Gets the canonical object for a specific implementation. The canonical object is used
   to call implementation-specific protocol functions where required (e.g. creation of new 
   arrays of the correct type for the implementation)"
  ([m]
    (let [k (get-implementation-key m)
          obj (@canonical-objects k)]
      (if k 
        (or obj
           (if (try-load-implementation k) (@canonical-objects k))
           (when-not (keyword? m) m)
           (error "Unable to find implementation: [" k "]"))
        nil))))

(defn construct 
  "Attempts to construct an array according to the type of array m. If not possible,
   returns another array type."
  ([m data]
    (or (gp/construct-matrix m data)
        ;; TODO: use current implementation?
        (gp/coerce-param m data)
        (gp/coerce-param [] data))))

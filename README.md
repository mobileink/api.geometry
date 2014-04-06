api.geometry
=============

A Clojure API for geometry.  See also dsl.geometry.

**CAVEAT** Experimental.  Will change drastically, often.

Partially inspired by
[core.matrix](https://github.com/mikera/core.matrix).  The idea is to
use Clojure's *protocol* facilities to define an API but efficiently
delegate implementation to plugins.  Unfortunately it's still early
days for core.matrix, so the documentation is pretty thin.  To get the
general idea see the comments in
[matrix.clj](https://github.com/mikera/core.matrix/blob/develop/src/main/clojure/clojure/core/matrix.clj)

Graphics languages:

 * [TikZ/PGF](http://www.ctan.org/pkg/pgf) The best of the lot.  Uses Knuthian (Metafont/post style) infix syntax, e.g. `(a,b) -- (c,d)` instead of `line a b c d`.
 * [Asymptote](http://asymptote.sourceforge.net/) also uses Metafont-like syntax.
 * [SeExpr](http://www.disneyanimation.com/technology/seexpr.html) Simple embeddable expression language "for procedural geometry synthesis, image synthesis, simulation control, and much more." (from Disney!)  Apache 2.0 license
 * [OS X GCGeometry](https://developer.apple.com/library/mac/documentation/graphicsimaging/Reference/CGGeometry/Reference/reference.html#//apple_ref/doc/uid/TP30000955-CH202-SW1)

See also:

 * [clojure/math.numerict-tower](https://github.com/clojure/math.numeric-tower)
 * [clojure/math.combinatorics](https://github.com/clojure/math.combinatorics)
 * [clojure-contrib math](http://richhickey.github.io/clojure-contrib/math-api.html)  - obsolete?
 * [clojure-math geometry](http://astanin.github.io/clojure-math/clojure.math.geometry.html)
 * [clojure-geometry](https://github.com/AndyMoreland/clojure-geometry)  (moribund?)
 * [euclidean](https://github.com/weavejester/euclidean)
 * search github for "geometry", lang=clojure

Java:

 * [JTS Topology Suite](http://www.vividsolutions.com/jts/main.htm) (moribund? - last release 2006? but sourceforge codebase seems to be actively maintained) see also [this](http://live.osgeo.org/en/overview/jts_overview.html)
 * [java.math](http://docs.oracle.com/javase/7/docs/api/java/lang/Math.html)
 * [EJML](https://code.google.com/p/efficient-java-matrix-library/) Efficient Java Matrix Library (EJML) "is a linear algebra library for manipulating dense matrices".  Apache v2.0 license
 * [jblas](http://mikiobraun.github.io/jblas/) "a fast linear algebra library for Java" based on BLAS and LAPACK
 * [JScience](http://jscience.org/) "tools and libraries for the advancement of science".  MIT Licence?
 * [Colt](http://acs.lbl.gov/software/colt/) "Open Source Libraries for High Performance Scientific and Technical Computing in Java." (CERN)
 * [JQuantLib](http://www.jquantlib.com/en/latest/) "comprehensive framework for quantitative finance, written in 100% Java."  BSD 2
 * [JDistlib](http://jdistlib.sourceforge.net/) Java Statistical Distribution Library.  Started 2012; GPL.  "A manual translation based on R" of various statistical distributions.
 * [Wikipedia list of java numerics libs](http://en.wikipedia.org/wiki/List_of_numerical_libraries#Java)
 * [JAMA java matrix lib](http://math.nist.gov/javanumerics/jama/)  (NIST)
 * [JavaNumerics](http://math.nist.gov/javanumerics/) - NIST portal (old)
 * [Apache Commons Math Lib](http://commons.apache.org/proper/commons-math/)
 * [SCaVis](http://jwork.org/scavis/) "an environment for scientific computation, data analysis and data visualization designed for scientists, engineers and students. The program incorporates many open-source software packages into a coherent interface using the concept of dynamic scripting."

[Java Number Cruncher: The Java Programmer's Guide to Numerical Computing](http://www.apropos-logic.com/nc/j (2002)

Commercial Java:

 * [JMSL Numerical Library](http://www.roguewave.com/products/imsl-numerical-libraries/java-library.aspx) Rogue Wave

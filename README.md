# Statisk
A library for data-driven static site generators.

## Introduction

_Statisk_ is a very small static site generator that I wrote to make non-blog-like sites portfolios. While primarily a fun/learning project, experience has shown me that the `.md` way of SSG proves a very crude fit to the type of pages that portfolios are comprised of. Instead, I'm trying out something open and data-driven (in the sense the Clojure community use the word) that might alleviate some of my precised frictions. 

_Statisk_ is completely dependency free, with an architecture that is heavily inspired by [_Reitit_](https://github.com/metosin/reitit) (albeit not as performance focus (yet)). It is important to note that every rebuild is done in full, no caching or other incremental shenanigans. This is by design – spitting out some web pages is insanely trivial. Any type of build complexion is bound to fail and get tripped up somewhere (esp. in my experience of other SSGs).

Unlike many other static site generators, _Statisk_ is not a framework or a program, but a library, each part offered a la carte. It is structured through the `pull -> transform -> push` methodology as conceptualized by Zach Tellman in [_Elements of Clojure_](https://elementsofclojure.com).

Please not that _Statisk_ is still in an early, semi-active development. That said, I would never release any backwards incompatible changes.

## Installation

Add the latest SHA to your `deps.edn`:

```clojure
org.motform/statisk {:git/url "https://github.com/motform/statisk"
                     :sha <latest-sha>}
```

## Usage

`motform.statisk.core` provides the primary API for re-building pages in a REPL environment. You could of course call it on the command line via `clj -X`, but that is not optimal unless you are doing a single build.

(There will be more instructions here later, including an example.)

## Licence
Copyright © 2021 [Love Lagerkvist](motform.org)

Distributed under the Eclipse Public License, the same as Clojure.

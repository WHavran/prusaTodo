# To-Do List (Java projekt)

## Model

Projekt je rozdělen na Entitu a DTO objekty.

- Entita `Task` představuje základní datový model:
  - `id`, `title`, `status`, `created`, `deadline`, `finished`, `description`
- Pro operace, kdy není potřeba všechna data (např. přehled nebo vytvoření nové položky), jsou použity DTO recordy.

Důraz byl kladen na škálovatelnost – model je navržen tak, aby bylo možné snadno přejít na relační databázi (např. pomocí JPA).

## Repository vrstva

Rozděleno na:
- Rozhraní (interface)
- Implementační třídu

Pro in-memory úložiště byla zvolena `TreeMap`, která simuluje chování relační databáze díky přístupu key-value. Umožňuje efektivní vyhledávání podle ID, zajišťuje unikátnost klíčů a konzistentní výkon.

### Přechod na databázi

Repository vrstva je navržena tak, aby ji bylo možné snadno nahradit např. pomocí JPA repository. Základní metody jako `findById`, `findAll` atd. odpovídají stylu běžně používanému v databázové vrstvě. Pouze složitější dotazy (např. `findSolvedThrewDays`) by bylo potřeba upravit.

## Service vrstva

- Rozděleno na rozhraní a implementaci.
- Vrstva zajišťuje aplikační logiku, získává data z repository a předává je controlleru.
- Využívá:
  - Mapper z balíčku `mapper` pro převod mezi entitami a DTO.
  - Privátní pomocnou metodu pro převod `List<T>` na `Page<T>` kvůli stránkování.

## Controller vrstva

Obsahuje:
- `TaskController`
- `GlobalExceptionHandler`

Cílem controlleru je mít čisté metody bez aplikační logiky – pouze příjem požadavků, delegování do service vrstvy a vrácení odpovědi.

### Ošetření chyb

`GlobalExceptionHandler` zajišťuje správné HTTP odpovědi při výskytu chyb.

## Testování

Projekt obsahuje sadu unit testů s použitím `MockMvc`. Testy se zaměřují na testování samotných endpointů.

## Architektura

V projektu je uplatněn architektonický vzor Controller – Service – Repository. Cílem bylo vytvořit strukturu, která umožní snadný přechod z in-memory úložiště na databázi s minimálními zásahy mimo repository vrstvu. Testy navíc pomáhají ověřit funkčnost při případné změně implementace.

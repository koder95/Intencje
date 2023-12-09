# Intencje
Program do zarządzania intencjami mszalnymi w bazie danych.
Współpracuje z dedykowaną wtyczką Wordpress, która pozwala na utworzenie wymaganych tabel, tworzenie i usuwanie intencji z poziomu zaplecza.
Do właściwego działania programu wymagany jest dostęp zdalny do bazy danych, choć w przyszłości marzeniem twórcy jest współpraca przez REST API.

## Wymagania
Uruchomienie programu wymaga zainstalowania środowiska uruchomieniowego Java 21 oraz systemu operacyjnego z GUI (choć kiedyś może się to zmieni, kto wie...).

Program współpracuje ze zdalnymi bazami danych mySQL, które posiadają tabele:
1. `%intencje` – posiada kolumny o nazwach
   * `msza` (typ: **datetime**),
   * `kaplica` (typ: **tinytext**),
   * `intencja` (typ: **text**)
   
   z głównym kluczem `msza`;
2. `%intencje_nazwy` – posiada kolumny o nazwach
   * `data` (typ: **date**),
   * `nazwa` (typ: **tinytext**)
   
   z głównym kluczem `data`;

Wtyczka Wordpress, której kod źródłowy jest tutaj dostępny, **NIE JEST polecana**, ponieważ bliżej jej do prototypu i tak należy ją traktować.
Działa, bo działa, ale jak działa to kwestia do oceny.
Na jej podstawie można zbudować własną wersję wtyczki.

## Rozwój oprogramowania
Nad rozwojem programu pracuje non-profit jedna osoba w czasie wolnym, dlatego aktualizacje i nowe funkcje będą pojawiały się nieregularnie i rzadko.

### Współpraca
Chętnych do współpracy nad oprogramowaniem proszę o wiadomość na adres [contrib@koder95.pl](mailto:contrib@koder95.pl).

# UDP-Multiple_Clients
UDP Server with multiple Clients Connection
Do zrobienia (oddzielać wykonane wiersze):
-Timer
-Odpowiedź tak,nie na przesyłaną przez klienta liczbę wysyłana na jego port  Przykład: OP?nie<<ID?id_klienta<<TM?czas<<
-Potwierdzenie otrzymania informacji (najlepiej w funkcji, żeby uniknąć powtarzania się kodu) Przykład: OP?Dostarczono<<TM?czas<<
-Wysyłanie informacji o zakończeniu się rozgrywki w przypadku odgadnięcia liczby (po kolei wysyłając do każdego klienta w mapie)
-Zachowanie informacji o czasie rozgrywki
-Ustawianie czasu rozgrywki [(id. sesji 1 + id. sesji 2) * 99] % 100 + 30. 

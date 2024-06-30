[33mcommit e7db15bd558a9c2b6387b879734763023042dfb6[m[33m ([m[1;36mHEAD -> [m[1;32mmain[m[33m, [m[1;31morigin/main[m[33m)[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Sat Jun 29 17:35:00 2024 +0200

    fix the problem with review add

[33mcommit d71450903086817ae846d0b191f98dfa9de03926[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Fri Jun 28 23:24:11 2024 +0200

    on the same problem, there is an issue with the bind. the causes can are: 1) ip address invalid 2) ip address already in use 3) anything else since gpt shoots bullshits

[33mcommit ea6c57867f7a1c4cac3f3a8366f15b9d8573b28f[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Fri Jun 28 23:08:52 2024 +0200

    fix problem with lock in server side, now on bind problema in client side (notification service)

[33mcommit 75a95b6ed7816638faa5f7c67a630a29f416e7df[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Fri Jun 28 17:26:43 2024 +0200

    syncronized data structures (hotelManagement and userManagement). Done bad synchronization with lock instead use java sync data structures

[33mcommit b497773b269b22eecfc73cea120cb8396b0b9d59[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Fri Jun 28 16:30:54 2024 +0200

    clean the code on client side (add some explaining comments and remove debug printing)

[33mcommit c5d4f50e00fdf78fc85ff684a3d31b8fa5196b06[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Fri Jun 28 16:09:07 2024 +0200

    clean the code on server side (add some explaining comments and remove debug printing)

[33mcommit 92836f04ccab84ce12cb0917e81762a1646cfd04[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Fri Jun 28 12:16:41 2024 +0200

    fix and doing notification services, done in server side (to review), to do in client side

[33mcommit 53cba4b47abe75a972747cff571204501222fd24[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Thu Jun 27 17:21:30 2024 +0200

    debugging review stuff, now the reqeuest arrive to the server, the switch (dispatcher) dispatch it correctly. NOW CHECKING HOTELMANAGER.ADDREVIEW(). Issues with review.fromString. ISSUE IN THE CLIENT: when adding review it write <<Index 1 out of bounds for length 1>>

[33mcommit afba6f0e0be0424527beb83bc7e6371b73e96970[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Thu Jun 27 15:42:18 2024 +0200

    start check reviews related components

[33mcommit 46093a6cd6ba7413c3a66dfb62e65e582ab1db62[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Thu Jun 27 14:10:05 2024 +0200

    big progress: the connection is almost ok; signin, login and logout are apparently ok. Have to check Serialization method and user saving (in json)

[33mcommit 900c4333e4e881b1ca49394c56b1987d2d12e30e[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Thu Jun 27 12:30:57 2024 +0200

    good progress with the connection, remain to understand why, after a request (successfully) the client send a msg with only a (space char) to the server, and it became crazy. TO VIEW THIS JUST TRY TO SIGN IN AND THEN LOG IN

[33mcommit 96f317d1b4d6c4d47721d065c42d9a8836dcb025[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Thu Jun 27 11:43:09 2024 +0200

    issue with last commit, lost (on cloud, not on local) a day of work

[33mcommit 5809d49373390d0b0da5ee72b9c731af954a510a[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Tue Jun 25 17:19:25 2024 +0200

    debug first error when activate server, have to continue to debug server

[33mcommit 50bd9df0844099bf849e4e6ffebbc2cd0f50688b[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Tue Jun 25 15:09:21 2024 +0200

    finish main implementation, starting with server debugging

[33mcommit 947788e4f2aea98950db338b30e2a8f9eaac181d[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Tue Jun 25 12:34:47 2024 +0200

    adjusting CLI: find error when reading from cmd, (in registration) the program can't read when using scanner.nextLine(). can fix that or use the Console obj to read

[33mcommit 26e4359941eeff57d0df9fe7685f7bae1d50c2f0[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Mon Jun 24 18:07:30 2024 +0200

    fix the error with the city in the review

[33mcommit 71162e1e33ce52b6e06acededfd91ef174a303bf[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Fri Jun 14 20:39:52 2024 +0200

    start with client. find a HUGE ERROR: forgot city parameter when insert review, have to modify cli, client file, and server side

[33mcommit f4482a7ae2d1c65fa5ae468f7698b3c1b4456c91[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Fri Jun 14 11:36:26 2024 +0200

    try to remove the warnings in server side

[33mcommit b368962929d39a9a3506755d86aca3f2bc0a512c[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Thu Jun 13 16:50:47 2024 +0200

    DONE SERVER SIDE, only reamin to test it and check some warning in HOTELIERServer.java

[33mcommit e24bb66730eb69dc7ba374b4417c0f646503db9c[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Thu Jun 13 12:25:36 2024 +0200

    forgot to commit - now on serverMain and requestHandling

[33mcommit bc104d98a1323c8cc3bc1c962536bae708965fc0[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Mon Jun 10 14:18:46 2024 +0200

    temp

[33mcommit 78b1aeb6914a51b6b97f7214aeaa15e00b3a2479[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Thu Jun 6 15:11:47 2024 +0200

    done milestone 3.7, in progress 3.8 (main method of server)

[33mcommit 77bc70710ebe392f78627584347a0599e11db6bd[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Thu Jun 6 15:07:22 2024 +0200

    done milestone 3.6 with multicast socket

[33mcommit d111e53a2e109c0c683d843242be71de5a9a9ee7[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Tue Jun 4 17:59:08 2024 +0200

    in progress milestone 7(DataPersistence), todo milestone 6 (NotificationService)

[33mcommit 1982febf11784b6e7a30f5727be09b6b5a2a30d8[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Tue Jun 4 17:27:38 2024 +0200

    in progress milestone 6 (notification sservice) - done milestone 5

[33mcommit 7fdc32f23f47ac2da4c829ddf1fcbbac9bc3f678[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Mon Jun 3 18:33:03 2024 +0200

    no task completed, work on updating hotel ranking

[33mcommit 9574035d0785f19f95988ced4006f13aa784a87a[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Wed May 29 19:58:25 2024 +0200

    in progress 3.3: implementazione delle componenti per la gestione degli hotel, in particolare la ricerca degli hotel in una città

[33mcommit cc2660f94043facaa71e57759c1501d4839244df[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Wed May 29 12:14:59 2024 +0200

    in progress 3.1, 3.2 (server side: server and usermanager component

[33mcommit 02db2cf1b1e3de7c1e7b4d8fa3568948ce6a3ae3[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Wed May 29 10:51:47 2024 +0200

    milestone 2.2 - done serialization and deserialization methods

[33mcommit dc2f357234158730691c8f7e4ab5885ebe4ede5e[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Wed May 29 10:50:09 2024 +0200

    milestone 1.2

[33mcommit 0d65b37992054ef217124e1ada7d9c17a24a5e4b[m
Author: Niccolò Fulgaro <nicco.fulgaro@outlook.it>
Date:   Tue May 28 18:08:24 2024 +0200

    milestone 1.1 - initalized project

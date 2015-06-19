The private exchange uses a shared Redisson map as a database. There can be
multiple exchanges running, but they all share this database.

The database has 2 keys, "subscribers" and "publishers". The subscribers are RTB exchanges
and the publishers own web pages.

The exchange listens for publishers to POST a campaign auction request. The exchange receiving
this request creates a Redisson shared object of the instance+publisherkey id, that is the bid ID, this
object contains the request and an empty map of bid responses.

A Redis publish is sent that all exchanges hear that identify the instance-publisherkey. Each exchange
creates a bid request and transmits on all open channels. and copies any 200 responses into the object's response map. If > 100ms
elapsed any response is ignored.

The exchange transmitting the original request waits 100ms for all other exchanges to answer, then reads the map back.
The keySet of bid responses is iterated and each is stored in an array, sorted by price. The bid with the highest price wins.
The win is written back to a connected exchange, at random, which handles the win notification.

The ADM that comes back is then read back by the original exchange, which returns the image reference.
# Dylan

Runs an scp server that accepts files and stores them encrypted, using a public key.

## Basics

Scp plus three APIs are available to interact with Dylan:

 * Upload files to Dylan using scp.
 * GET `/list` to list uploaded files.
 * GET `/key/name` to retrieve an encrypted key for a file. This key can be decrypted using the private key corresponding to the public key used for encryption.
 * GET `/file/name` to retrieve an encrypted file. This file can be decrypted using the recovered key.
 

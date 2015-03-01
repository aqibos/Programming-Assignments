<h1>File-Transfer Protocol (FTP) Server</h1>

<p>Java program that acts as an FTP server. It is compatible with the Windows built-in FTP client.</p>

<p>Only the following functions were implemented:</p>
<ul>
<li><b>user</b> and <b>password</b> - Allows the client to login if the provided 'user' and 'password' are the same String, and set the current working directory of the client as the root directory.</li>
<li><b>mkdir remote-path</b> - Create a new directory in the server, where 'remote-path' gives the relative path.</li>
<li><b>cd remote-path</b> - Change the working directory of the client on the server to 'remote-path'.</li>
<li><b>pwd</b> - Return the working directory of the client.</li>
<li><b>get remote-file</b> - Send the file named 'remote-file' from the server to the client.</li>
<li><b>put local-file</b> - Recieve the file named 'local-file' from the client to the server, and save it in the working directory with the same file name.</li>
<li><b>delete remote-file</b> - Delete the file named 'remote-file' from the server.</li>
<li><b>quit</b> - Disconnect the FTP client.</li>

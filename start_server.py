import subprocess
import os
import sys

jdk_javaw = r"C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot\bin\javaw.exe"
jar = r"C:\CODING\Projects\pharmacy-app\pharmacy-app.jar"
log_file = open(r"C:\CODING\Projects\pharmacy-app\server.log", "a", encoding="utf-8")

DETACHED_PROCESS = 0x00000008
CREATE_NEW_PROCESS_GROUP = 0x00000200

p = subprocess.Popen(
    [jdk_javaw, "-jar", jar],
    cwd=r"C:\CODING\Projects\pharmacy-app",
    stdout=log_file,
    stderr=subprocess.STDOUT,
    creationflags=DETACHED_PROCESS | CREATE_NEW_PROCESS_GROUP,
    close_fds=True
)
print(f"Launched detached server PID: {p.pid}")

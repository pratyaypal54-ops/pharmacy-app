import os
import sys
import zipfile
import subprocess
import shutil
import glob

PROJECT_DIR = r"C:\CODING\Projects\pharmacy-app"
JDK_BIN = r"C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot\bin"
JAVAC = os.path.join(JDK_BIN, "javac.exe")
JAR_EXE = os.path.join(JDK_BIN, "jar.exe")
JAR_PATH = os.path.join(PROJECT_DIR, "pharmacy-app.jar")
LIBS_DIR = os.path.join(PROJECT_DIR, "target", "extracted-libs")
CLASSES_DIR = os.path.join(PROJECT_DIR, "target", "classes")
JAR_UPDATE_DIR = os.path.join(PROJECT_DIR, "target", "jar-update")
BOOT_INF_CLASSES = os.path.join(JAR_UPDATE_DIR, "BOOT-INF", "classes")

LOMBOK_JAR = r"C:\Users\praty\.m2\repository\org\projectlombok\lombok\1.18.34\lombok-1.18.34.jar"

def main():
    print("=== Step 1: Extracting libraries from JAR if needed ===")
    os.makedirs(LIBS_DIR, exist_ok=True)
    existing_jars = glob.glob(os.path.join(LIBS_DIR, "*.jar"))
    if not existing_jars:
        print(f"Extracting BOOT-INF/lib from {JAR_PATH}...")
        with zipfile.ZipFile(JAR_PATH, 'r') as z:
            for item in z.namelist():
                if item.startswith("BOOT-INF/lib/") and item.endswith(".jar"):
                    filename = os.path.basename(item)
                    dest_file = os.path.join(LIBS_DIR, filename)
                    with z.open(item) as src, open(dest_file, "wb") as dst:
                        shutil.copyfileobj(src, dst)
        existing_jars = glob.glob(os.path.join(LIBS_DIR, "*.jar"))
        print(f"Extracted {len(existing_jars)} dependency JARs.")
    else:
        print(f"Using {len(existing_jars)} cached dependency JARs.")

    cp_list = list(existing_jars)
    if os.path.exists(LOMBOK_JAR):
        cp_list.append(LOMBOK_JAR)
    classpath = ";".join(cp_list)

    print("=== Step 2: Compiling Java source files ===")
    os.makedirs(CLASSES_DIR, exist_ok=True)
    java_files = []
    for root, dirs, files in os.walk(os.path.join(PROJECT_DIR, "src", "main", "java")):
        for f in files:
            if f.endswith(".java"):
                java_files.append(os.path.join(root, f).replace("\\", "/"))
    print(f"Found {len(java_files)} Java files to compile.")

    # Write source file list to an argfile with forward slashes
    argfile_path = os.path.join(PROJECT_DIR, "target", "sources.txt")
    with open(argfile_path, "w", encoding="utf-8") as f:
        for jf in java_files:
            f.write(f'"{jf}"\n')

    cmd = [
        JAVAC,
        "-parameters",
        "-encoding", "UTF-8",
        "-cp", classpath,
        "-processorpath", LOMBOK_JAR,
        "-d", CLASSES_DIR,
        "@" + argfile_path.replace("\\", "/")
    ]
    res = subprocess.run(cmd, capture_output=True, text=True)
    if res.returncode != 0:
        print("Compilation FAILED:")
        print(res.stderr)
        print(res.stdout)
        sys.exit(1)
    print("Compilation SUCCESSFUL with -parameters flag.")

    print("=== Step 3: Preparing BOOT-INF/classes payload ===")
    os.makedirs(BOOT_INF_CLASSES, exist_ok=True)
    # Copy compiled classes
    shutil.copytree(CLASSES_DIR, BOOT_INF_CLASSES, dirs_exist_ok=True)
    # Copy resources (templates, static, application.properties)
    resources_dir = os.path.join(PROJECT_DIR, "src", "main", "resources")
    shutil.copytree(resources_dir, BOOT_INF_CLASSES, dirs_exist_ok=True)
    print("Copied compiled classes and resources to staging directory.")

    print("=== Step 4: Updating pharmacy-app.jar ===")
    jar_cmd = [
        JAR_EXE,
        "-uf", JAR_PATH,
        "-C", JAR_UPDATE_DIR,
        "BOOT-INF"
    ]
    res_jar = subprocess.run(jar_cmd, capture_output=True, text=True, cwd=PROJECT_DIR)
    if res_jar.returncode != 0:
        print("JAR update FAILED:")
        print(res_jar.stderr)
        print(res_jar.stdout)
        sys.exit(1)
    print(f"Successfully updated {JAR_PATH}.")

if __name__ == "__main__":
    main()

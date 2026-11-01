import os, subprocess, shutil

javac = r'C:\Users\Matheus\AppData\Roaming\PrismLauncher\java\java-runtime-delta\bin\javac.exe'
jar_exe = r'C:\Users\Matheus\AppData\Roaming\PrismLauncher\java\java-runtime-delta\bin\jar.exe'
prism_libs = r'C:\Users\Matheus\AppData\Roaming\PrismLauncher\libraries'

classpath_jars = [
    os.path.join(prism_libs, r'net\minecraft\client\1.21.1-20240808.144430\client-1.21.1-20240808.144430-srg.jar'),
    os.path.join(prism_libs, r'net\neoforged\neoforge\21.1.256\neoforge-21.1.256-universal.jar'),
    os.path.join(prism_libs, r'net\neoforged\fancymodloader\loader\4.0.45\loader-4.0.45.jar'),
    os.path.join(prism_libs, r'net\neoforged\bus\8.0.5\bus-8.0.5.jar'),
    os.path.join(prism_libs, r'net\neoforged\mergetool\2.0.0\mergetool-2.0.0-api.jar'),
    os.path.join(prism_libs, r'com\mojang\datafixerupper\8.0.16\datafixerupper-8.0.16.jar'),
    os.path.join(prism_libs, r'org\slf4j\slf4j-api\2.0.9\slf4j-api-2.0.9.jar'),
    os.path.join(prism_libs, r'com\google\code\gson\gson\2.10.1\gson-2.10.1.jar'),
    os.path.join(prism_libs, r'com\google\guava\guava\32.1.2-jre\guava-32.1.2-jre.jar'),
    os.path.join(prism_libs, r'com\mojang\brigadier\1.2.9\brigadier-1.2.9.jar'),
    os.path.join(prism_libs, r'cpw\mods\modlauncher\11.0.5\modlauncher-11.0.5.jar'),
    os.path.join(prism_libs, r'io\netty\netty-buffer\4.1.97.Final\netty-buffer-4.1.97.Final.jar'),
    os.path.join(prism_libs, r'io\netty\netty-common\4.1.97.Final\netty-common-4.1.97.Final.jar'),
    os.path.join(prism_libs, r'io\netty\netty-codec\4.1.97.Final\netty-codec-4.1.97.Final.jar'),
    os.path.join(prism_libs, r'io\netty\netty-transport\4.1.97.Final\netty-transport-4.1.97.Final.jar')
]

project_dir = r'C:\Users\Matheus\AppData\Roaming\PrismLauncher\instances\Base Modpack\d20chaos-project'
src_dir = os.path.join(project_dir, 'src', 'com', 'd20chaos')
build_dir = os.path.join(project_dir, 'build')
classes_dir = os.path.join(build_dir, 'classes')

if os.path.exists(classes_dir):
    shutil.rmtree(classes_dir)
os.makedirs(classes_dir, exist_ok=True)

java_files = []
for root, dirs, files in os.walk(os.path.join(project_dir, 'src')):
    for f in files:
        if f.endswith('.java'):
            java_files.append(os.path.join(root, f))
print('Compiling %d files...' % len(java_files))

cp_str = ';'.join(classpath_jars)
cmd_compile = [javac, '-encoding', 'UTF-8', '-cp', cp_str, '-d', classes_dir] + java_files

res = subprocess.run(cmd_compile, capture_output=True, text=True)
print('STDOUT:', res.stdout)
print('STDERR:', res.stderr)
print('Exit code:', res.returncode)

if res.returncode == 0:
    # Copy all resources (META-INF, assets, models, textures)
    res_dir = os.path.join(project_dir, 'resources')
    if os.path.exists(res_dir):
        shutil.copytree(res_dir, classes_dir, dirs_exist_ok=True)
    
    # Package jar
    target_jar = os.path.join(build_dir, 'd20chaos-1.0.0.jar')
    cmd_jar = [jar_exe, '-cf', target_jar, '-C', classes_dir, '.']
    res_jar = subprocess.run(cmd_jar, capture_output=True, text=True)
    print('JAR exit code:', res_jar.returncode)

    # Copy jar to mods
    mods_dir = r'C:\Users\Matheus\AppData\Roaming\PrismLauncher\instances\Base Modpack\minecraft\mods'
    dest_jar = os.path.join(mods_dir, 'd20chaos-1.0.0.jar')
    shutil.copy(target_jar, dest_jar)
    print('SUCCESS! d20chaos-1.0.0.jar deployed to:', dest_jar)
else:
    print('COMPILATION FAILED!')

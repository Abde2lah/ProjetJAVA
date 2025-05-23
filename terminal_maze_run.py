import subprocess
import sys

def run_interactive_java_command():
    command = ["java", "-cp", "app/build/classes/java/main", "org.mazeApp.Launcher", "terminal"]

    try:
        # Launch the Java process with full I/O inheritance
        subprocess.run(command, stdin=sys.stdin, stdout=sys.stdout, stderr=sys.stderr)
    except FileNotFoundError:
        print("Java not found. Please make sure it's installed and added to your PATH.")
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    run_interactive_java_command()


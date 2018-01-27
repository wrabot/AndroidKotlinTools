# Android Kotlin Tools

Set of tools that in my projects

nullable: checks by reflection that kotlin mandatory members are really not null.
This is useful when kotlin is created with java reflection (like with Gson).

preferences: maps kotlin properties to SharePreferences easily
 
rx-adapter: use databinding & RX to easily use recycler views

rx-fingerprint: cipher & decipher data with fingerprint through RX

rx-foreground: create an observable wich detects application background/foreground

rx-retrofit: create a call adapter factory for RX with the ability to add retry

## installation

Add the following repository

    repositories {
        maven {
        }
    }
    
Add the following dependencies

    dependencies {
    }

# IDS - Intrusion Detection System

A Java-based Intrusion Detection System (IDS) with a Swing GUI, packet capture/analysis pipeline, rule engine, and alerting integrations.

## Table of Contents
1. Overview
2. Features
3. Tech Stack
4. Project Structure
5. Prerequisites
6. Quick Start (Recommended)
7. Running with Docker
8. How the App Starts and Works
9. GUI Usage Guide
10. Rules Format and Validation
11. Troubleshooting
12. Development Notes

## 1. Overview
This project captures packets (live or from PCAP files), parses them, evaluates them against security rules, stores packet data, and visualizes packet/alert activity in a modern dark-mode GUI.

## 2. Features
- Live packet capture mode
- Offline PCAP/PCAPNG analysis mode
- Rule-based matching and alert generation
- Packet and alert visualization in GUI pages
- Elasticsearch indexing/search for packet data
- MySQL-backed rule/alert persistence
- Dark cybersecurity-themed UI

## 3. Tech Stack
- Java 21
- Maven
- Swing (GUI)
- Pcap4J
- MySQL + HikariCP
- Elasticsearch Java Client
- SLF4J + Log4j2
- Docker / Docker Compose

## 4. Project Structure
- `src/main/java/com/example/Main.java`: application entry point
- `src/main/java/com/example/engine`: engine lifecycle and observer notifications
- `src/main/java/com/example/consumer` + `producer`: packet pipeline
- `src/main/java/com/example/util`: packet parsing, regex, rule validation
- `src/main/java/com/example/gui`: GUI windows/pages
- `src/main/resources`: assets and logging config

## 5. Prerequisites
- JDK 21
- Maven 3.9+
- Npcap/WinPcap compatible setup for live capture (Windows)
- MySQL and Elasticsearch reachable (if using full persistence/search features)

## 6. Quick Start (Recommended)
Run from project root (`d:\omar\college\git projects\IDS`):

```powershell
mvn clean compile
mvn --% exec:java -Dexec.mainClass=com.example.Main
```

Or package then run:

```powershell
mvn clean package
java -cp target/IDS-1.0-SNAPSHOT.jar com.example.Main
```

Note: using plain `javac Main.java` is not the correct way for this project because it uses packages + external dependencies.

## 7. Running with Docker
See [README.Docker.md](README.Docker.md) for containerized setup.

## 8. How the App Starts and Works
1. `com.example.Main` launches the welcome screen on Swing EDT.
2. User opens analyzer pages from GUI.
3. `EngineIds` starts producers/consumer threads (live or PCAP mode).
4. Consumer parses packets and matches rules.
5. Parsed packet/alert events are pushed via observer pattern to GUI.
6. GUI updates packet table and alert table in real time.

## 9. GUI Usage Guide
### Welcome / Navigation
- Start from welcome screen
- Open analyzer pages
- Access rules/settings/about from UI controls

### Network Analyzer
- Choose mode: `Live Packet Data` or `PCAP File`
- For PCAP mode, select a `.pcap` or `.pcapng`
- Click `Start` to begin processing, `Stop` to end
- Use search field to filter visible rows
- Select row to inspect packet details and raw payload

### Alerts Page
- Displays alerts emitted by rule matches
- Select an alert row for detailed view
- Export alerts as CSV if needed

### Rules Page
- Add/Edit/Delete rules
- Import/Export CSV
- Rule input is validated before saving

## 10. Rules Format and Validation
Rule fields (7 tokens):
1. Protocol
2. Source MAC
3. Source IP
4. Source Port
5. Destination MAC
6. Destination IP
7. Destination Port

Accepted wildcard: `Any` (case-insensitive)

Validation includes:
- Protocol allow-list (e.g., TCP/UDP/ICMP/DNS/ARP/HTTP/HTTPS/SCTP)
- MAC address format
- IPv4/IPv6/localhost handling
- Port range `0-65535`

## 11. Troubleshooting
### Error you reported
If you run:

```powershell
javac Main.java
java Main
```

from `src/main/java/com/example`, you get package errors like:
- `package com.example.gui does not exist`

Reason: `Main.java` belongs to package `com.example`, and this project requires package-root compilation plus external dependencies.

Fix: run with Maven from project root:

```powershell
mvn clean compile
mvn --% exec:java -Dexec.mainClass=com.example.Main
```

### GUI opens but packet capture fails
- Ensure packet capture driver is installed (Npcap/WinPcap)
- Run terminal/IDE with sufficient privileges if required by capture backend

### DB/Elasticsearch-related failures
- Confirm `.env` values and services are reachable
- Check `logs/app.log` for full stack traces

## 12. Development Notes
- Build check:

```powershell
mvn -DskipTests compile
```

- Logging config:
- `src/main/resources/log4j2.xml`

- Keep package structure intact when adding classes.

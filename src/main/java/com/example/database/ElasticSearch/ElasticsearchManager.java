package com.example.database.ElasticSearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.mapping.KeywordProperty;
import co.elastic.clients.elasticsearch._types.mapping.TextProperty;
import co.elastic.clients.elasticsearch._types.mapping.IntegerNumberProperty;
import co.elastic.clients.elasticsearch._types.mapping.IpProperty;

public class ElasticsearchManager {
    private final ElasticsearchClient client;

    public ElasticsearchManager() {
        RestClient restClient = RestClient.builder(
            new HttpHost("localhost", 9200)
        ).build();

        ElasticsearchTransport transport = new RestClientTransport(
            restClient,
            new JacksonJsonpMapper()
        );

        this.client = new ElasticsearchClient(transport);
    }

    public boolean indexExists(String indexName) {
        try {
            return client.indices().exists(e -> e.index(indexName)).value();
        } catch (Exception e) {
            System.err.println("Error checking index existence: " + e.getMessage());
            return false;
        }
    }

    // Delete an index (use with caution!)
    public void deleteIndex(String indexName) {
        try {
            client.indices().delete(d -> d.index(indexName));
            System.out.println("Index '" + indexName + "' deleted!");
        } catch (Exception e) {
            System.err.println("Failed to delete index: " + e.getMessage());
        }
    }

    public void createPacketIndex() {
        try {
            client.indices().create(c -> c
                .index("packets")
                .mappings(m -> m
                    .properties("ethernet", ethProp -> ethProp.object(ethObj -> ethObj
                        .properties("source", srcProp -> srcProp.keyword(KeywordProperty.of(k -> k)))
                        .properties("destination", destProp -> destProp.keyword(KeywordProperty.of(k -> k)))
                        .properties("type", typeProp -> typeProp.keyword(KeywordProperty.of(k -> k)))
                    ))
                    .properties("ipv4", ipv4Prop -> ipv4Prop.object(ipv4Obj -> ipv4Obj
                        .properties("sourceIp", srcIpProp -> srcIpProp.ip(IpProperty.of(i -> i)))
                        .properties("destinationIp", destIpProp -> destIpProp.ip(IpProperty.of(i -> i)))
                        .properties("protocol", protoProp -> protoProp.keyword(KeywordProperty.of(k -> k)))
                        .properties("ttl", ttlProp -> ttlProp.integer(IntegerNumberProperty.of(n -> n)))
                    ))
                    .properties("udp", udpProp -> udpProp.object(udpObj -> udpObj
                        .properties("sourcePort", srcPortProp -> srcPortProp.integer(IntegerNumberProperty.of(n -> n)))
                        .properties("destinationPort", destPortProp -> destPortProp.integer(IntegerNumberProperty.of(n -> n)))
                    ))
                    .properties("dns", dnsProp -> dnsProp.object(dnsObj -> dnsObj
                        .properties("qname", qnameProp -> qnameProp.keyword(KeywordProperty.of(k -> k)))
                        .properties("qtype", qtypeProp -> qtypeProp.keyword(KeywordProperty.of(k -> k)))
                    ))
                    .properties("rawPacket", rawProp -> rawProp.text(TextProperty.of(t -> t)))
                )
            );
        } catch (Exception e) {
            System.err.println("Failed to create index: " + e.getMessage());
        }
    }

    public void indexPacket(PacketData packet) {
        try {
            IndexResponse response = client.index(i -> i
                .index("packets")
                .document(packet)
                .refresh(Refresh.True)
            );
            System.out.println("Packet indexed with ID: " + response.id());
        } catch (Exception e) {
            System.err.println("Failed to index packet: " + e.getMessage());
        }
    }

    public void searchByDestinationIp(String ip) {
        try {
            SearchResponse<PacketData> response = client.search(s -> s
                .index("packets")
                .query(q -> q.term(t -> t.field("ipv4.destinationIp").value(ip))),
                PacketData.class
            );
            printResults(response);
        } catch (Exception e) {
            System.err.println("Search failed: " + e.getMessage());
        }
    }

    public void searchBySourcePort(int port) {
        try {
            SearchResponse<PacketData> response = client.search(s -> s
                .index("packets")
                .query(q -> q.term(t -> t.field("udp.sourcePort").value(port))),
                PacketData.class
            );
            printResults(response);
        } catch (Exception e) {
            System.err.println("Search failed: " + e.getMessage());
        }
    }

    public void searchBySourceIp(String ip) {
        try {
            SearchResponse<PacketData> response = client.search(s -> s
                .index("packets")
                .query(q -> q.term(t -> t.field("ipv4.sourceIp").value(ip))),
                PacketData.class
            );
            printResults(response);
        } catch (Exception e) {
            System.err.println("Search failed: " + e.getMessage());
        }
    }
    public void searchByProtocol(String protocol) {
        try {
            SearchResponse<PacketData> response = client.search(s -> s
                .index("packets")
                .query(q -> q.term(t -> t.field("ipv4.protocol").value(protocol))),
                PacketData.class
            );
            printResults(response);
        } catch (Exception e) {
            System.err.println("Search failed: " + e.getMessage());
        }
    }

    public void searchByDestinationPort(int port) {
        try {
            SearchResponse<PacketData> response = client.search(s -> s
                .index("packets")
                .query(q -> q.term(t -> t.field("udp.destinationPort").value(port))),
                PacketData.class
            );
            printResults(response);
        } catch (Exception e) {
            System.err.println("Search failed: " + e.getMessage());
        }
    }

    public void searchByTTLRange(int min, int max) {
        try {
            SearchResponse<PacketData> response = client.search(s -> s
                .index("packets") // Use the correct index name ("packets", not "network_packets")
                .query(q -> q
                    .range(r -> r
                        .field("ipv4.ttl")
                        .gte(JsonData.of(min))  // "Greater than or equal to" (gte)
                        .lte(JsonData.of(max))  // "Less than or equal to" (lte)
                    )
                ),
                PacketData.class
            );
            printResults(response); // Renamed from printHits() to match your existing method
        } catch (Exception e) {
            System.err.println("Search failed: " + e.getMessage());
        }
    }


    public void searchByDnsQuery(String qname) {
        try {
            SearchResponse<PacketData> response = client.search(s -> s
                .index("packets")
                .query(q -> q.term(t -> t.field("dns.qname").value(qname))),
                PacketData.class
            );
            printResults(response);
        } catch (Exception e) {
            System.err.println("Search failed: " + e.getMessage());
        }
    }

    public void searchInRawText(String keyword) {
        try {
            SearchResponse<PacketData> response = client.search(s -> s
                .index("packets")
                .query(q -> q.match(m -> m.field("rawPacket").query(keyword))),
                PacketData.class
            );
            printResults(response);
        } catch (Exception e) {
            System.err.println("Search failed: " + e.getMessage());
        }
    }

    private void printResults(SearchResponse<PacketData> response) {
        System.out.println("Found " + response.hits().hits().size() + " packets:");
        for (Hit<PacketData> hit : response.hits().hits()) {
            PacketData p = hit.source();
            System.out.println("\n[Packet ID: " + hit.id() + "]");
            System.out.println("Source IP: " + p.getIpv4().getSourceIp());
            System.out.println("Destination IP: " + p.getIpv4().getDestinationIp());
            System.out.println("Protocol: " + p.getIpv4().getProtocol());       // ADDED
            System.out.println("Destination Port: " + p.getUdp().getDestinationPort()); // ADDED
            System.out.println("TTL: " + p.getIpv4().getTtl());                 // ADDED
            System.out.println("DNS QTYPE: " + p.getDns().getQtype());          // ADDED
            System.out.println("Raw Packet: \n" + p.getRawPacket());
        }
    }
}
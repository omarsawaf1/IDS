package com.example.database.ElasticSearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.Refresh;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ElasticsearchManager {
    private static final Dotenv dotenv = Dotenv.load();
    private static final RestClient restClient;
    private static final ElasticsearchClient client;

    static {
        String dbHost = dotenv.get("DB_HOSTELASTICSEARCH");
        int dbPort = Integer.parseInt(dotenv.get("DB_PORTELASTICSEARCH"));
        restClient = RestClient.builder(new HttpHost(dbHost, dbPort)).build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        client = new ElasticsearchClient(transport);
    }

    public boolean indexExists(String indexName) {
        try {
            return client.indices().exists(e -> e.index(indexName)).value();
        } catch (Exception e) {
            System.err.println("Error checking index existence: " + e.getMessage());
            return false;
        }
    }

    public void deleteIndex(String indexName) {
        try {
            client.indices().delete(d -> d.index(indexName));
            System.out.println("Index '" + indexName + "' deleted!");
        } catch (Exception e) {
            System.err.println("Failed to delete index: " + e.getMessage());
        }
    }

    public boolean indexUserPacket(int userId, String rawContent) {
        String indexName = "user_" + userId;

        try {
            if (!indexExists(indexName)) {
                client.indices().create(c -> c.index(indexName)
                        .mappings(m -> m.properties("raw", p -> p.text(t -> t))));
            }

            RawPacket packet = new RawPacket(rawContent);
            IndexResponse response = client.index(i -> i
                    .index(indexName)
                    .document(packet)
                    .refresh(Refresh.True));
            System.out.println("Indexed in " + indexName + " ID: " + response.id());
            return true;
        } catch (Exception e) {
            System.err.println("Error in indexUserPacket: " + e.getMessage());
            return false;
        }
    }

    public List<String> searchUserPackets(int userId, String keyword) {
        String indexName = "user_" + userId;
        List<String> results = new ArrayList<>();

        try {
            if (!indexExists(indexName)) {
                return results;
            }

            SearchResponse<RawPacket> response = client.search(s -> s
                            .index(indexName)
                            .query(q -> q.match(m -> m.field("raw").query(keyword))),
                    RawPacket.class);

            for (Hit<RawPacket> hit : response.hits().hits()) {
                results.add(hit.source().getRaw());
            }
        } catch (Exception e) {
            System.err.println("Search failed: " + e.getMessage());
        }

        return results;
    }
    public String[] printUserData(int userId) {
        String indexName = "user_" + userId;
        List<String> results = new ArrayList<>();

        try {
            if (!indexExists(indexName)) {
                System.out.println("No index found for user " + userId);
                return new String[0];
            }

            SearchResponse<RawPacket> response = client.search(s -> s
                            .index(indexName)
                            .query(q -> q.matchAll(m -> m)),
                    RawPacket.class);

            for (Hit<RawPacket> hit : response.hits().hits()) {
                String packet = hit.source().getRaw();
                System.out.println("User " + userId + " Packet: " + packet);
                results.add(packet);
            }
        } catch (Exception e) {
            System.err.println("Search failed: " + e.getMessage());
        }

        return results.toArray(new String[0]);
    }
    public void createPacketIndex() {
        try {
            client.indices().create(c -> c.index("packets")
                    .mappings(m -> m
                            .properties("ethernet", eth -> eth.object(o -> o
                                    .properties("source", f -> f.keyword(k -> k))
                                    .properties("destination", f -> f.keyword(k -> k))
                                    .properties("type", f -> f.keyword(k -> k))))
                            .properties("ipv4", ipv4 -> ipv4.object(o -> o
                                    .properties("sourceIp", f -> f.ip(i -> i))
                                    .properties("destinationIp", f -> f.ip(i -> i))
                                    .properties("protocol", f -> f.keyword(k -> k))
                                    .properties("ttl", f -> f.integer(n -> n))))
                            .properties("udp", udp -> udp.object(o -> o
                                    .properties("sourcePort", f -> f.integer(n -> n))
                                    .properties("destinationPort", f -> f.integer(n -> n))))
                            .properties("dns", dns -> dns.object(o -> o
                                    .properties("qname", f -> f.keyword(k -> k))
                                    .properties("qtype", f -> f.keyword(k -> k))))
                            .properties("rawPacket", raw -> raw.text(t -> t))
                    ));
        } catch (Exception e) {
            System.err.println("Failed to create packet index: " + e.getMessage());
        }
    }

    public void indexPacket(PacketData packet) {
        try {
            IndexResponse response = client.index(i -> i
                    .index("packets")
                    .document(packet)
                    .refresh(Refresh.True));
            System.out.println("Packet indexed with ID: " + response.id());
        } catch (Exception e) {
            System.err.println("Failed to index packet: " + e.getMessage());
        }
    }

    public void searchBySourceIp(String ip) {
        searchGeneric("ipv4.sourceIp", ip);
    }

    public void searchByDestinationIp(String ip) {
        searchGeneric("ipv4.destinationIp", ip);
    }

    public void searchByProtocol(String protocol) {
        searchGeneric("ipv4.protocol", protocol);
    }

    public void searchBySourcePort(int port) {
        searchGeneric("udp.sourcePort", port);
    }

    public void searchByDestinationPort(int port) {
        searchGeneric("udp.destinationPort", port);
    }

    public void searchByDnsQuery(String qname) {
        searchGeneric("dns.qname", qname);
    }

    public void searchInRawText(String keyword) {
        try {
            SearchResponse<PacketData> response = client.search(s -> s
                    .index("packets")
                    .query(q -> q.match(m -> m.field("rawPacket").query(keyword))),
                    PacketData.class);
            printResults(response);
        } catch (Exception e) {
            System.err.println("Raw text search failed: " + e.getMessage());
        }
    }

    public void searchByTTLRange(int min, int max) {
        try {
            SearchResponse<PacketData> response = client.search(s -> s
                            .index("packets")
                            .query(q -> q.range(r -> r
                                    .field("ipv4.ttl")
                                    .gte(JsonData.of(min))
                                    .lte(JsonData.of(max)))),
                    PacketData.class);
            printResults(response);
        } catch (Exception e) {
            System.err.println("TTL range search failed: " + e.getMessage());
        }
    }

    private void searchGeneric(String field, Object value) {
    try {
        FieldValue fieldValue;

        if (value instanceof String) {
            fieldValue = FieldValue.of((String) value);
        } else if (value instanceof Integer) {
            fieldValue = FieldValue.of((Integer) value);
        } else if (value instanceof Long) {
            fieldValue = FieldValue.of((Long) value);
        } else if (value instanceof Boolean) {
            fieldValue = FieldValue.of((Boolean) value);
        } else {
            throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
        }

        SearchResponse<PacketData> response = client.search(s -> s
                        .index("packets")
                        .query(q -> q.term(t -> t.field(field).value(fieldValue))),
                PacketData.class);

        printResults(response);
    } catch (Exception e) {
        System.err.println("Search failed on field " + field + ": " + e.getMessage());
    }
}



    private void printResults(SearchResponse<PacketData> response) {
        System.out.println("Found " + response.hits().hits().size() + " packets:");
        for (Hit<PacketData> hit : response.hits().hits()) {
            PacketData p = hit.source();
            System.out.println("\n[Packet ID: " + hit.id() + "]");
            System.out.println("Source IP: " + p.getIpv4().getSourceIp());
            System.out.println("Destination IP: " + p.getIpv4().getDestinationIp());
            System.out.println("Protocol: " + p.getIpv4().getProtocol());
            System.out.println("TTL: " + p.getIpv4().getTtl());
            System.out.println("Destination Port: " + p.getUdp().getDestinationPort());
            System.out.println("DNS QTYPE: " + p.getDns().getQtype());
            System.out.println("Raw Packet: \n" + p.getRawPacket());
        }
    }

    public static void shutdown() throws IOException {
        restClient.close();
    }
}
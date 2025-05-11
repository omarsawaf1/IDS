package com.example.database.ElasticSearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
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
        String dbPort = dotenv.get("DB_PORTELASTICSEARCH");
        int dbport = Integer.parseInt(dbPort);
        restClient = RestClient.builder(new HttpHost(dbHost, dbport)).build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        client = new ElasticsearchClient(transport);
    }

    public boolean indexUserPacket(int userId, String rawContent) {
        String indexName = "user_" + userId;

        try {
            if (!client.indices().exists(e -> e.index(indexName)).value()) {
                client.indices().create(c -> c.index(indexName)
                        .mappings(m -> m.properties("raw", p -> p.text(t -> t))));
            }

            RawPacket packet = new RawPacket(rawContent);
            IndexResponse response = client.index(i -> i
                    .index(indexName)
                    .document(packet)
                    .refresh(Refresh.True)
            );
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
            if (!client.indices().exists(e -> e.index(indexName)).value()) {
                return results;
            }

            SearchResponse<RawPacket> response = client.search(s -> s
                    .index(indexName)
                    .query(q -> q.match(m -> m.field("raw").query(keyword))),
                    RawPacket.class
            );

            for (Hit<RawPacket> hit : response.hits().hits()) {
                results.add(hit.source().getRaw());
            }

        } catch (Exception e) {
            System.err.println("Search failed: " + e.getMessage());
        }

        return results;
    }

    public static void shutdown() throws IOException {
        restClient.close();
    }
}


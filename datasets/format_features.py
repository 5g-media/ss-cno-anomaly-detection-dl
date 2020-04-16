import ast

metrics_min_max = {
    'packets_sent_rate': 20.652548916525653,
    'http.current_active_client_connections': 18,
    'bytes_recv_rate': 27.57807896287566,
    'http.user_agent_current_connections_count': 19,
    'response_data_total_bandwidth': 397926,
    'response_data_lan_bandwidth': 375961,
    'response_data_wan_bandwidth': 42287,
    'bytes_sent_rate': 27.008863758774552,
    'hostdb.cache.total_hits_rate': 0.38888052586682725,
    'response_data_stream_count_direct_play': 3,
    'response_data_stream_count': 3,
    'packets_recv_rate': 26.666666666666668,
    'http.current_client_connections': 19
}

with open('UHDoCDN_irregular_conditions_edge_cache.txt') as f:
    for line in f:
        metrics = ast.literal_eval(line)
        print('Array({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}),'.format(
            (metrics['bytes_sent_rate'] / metrics_min_max['bytes_sent_rate']),
            (metrics['bytes_recv_rate'] / metrics_min_max['bytes_recv_rate']),
            (metrics['packets_sent_rate'] / metrics_min_max['packets_sent_rate']),
            (metrics['packets_recv_rate'] / metrics_min_max['packets_recv_rate']),
            (metrics['http.current_active_client_connections'] / metrics_min_max['http.current_active_client_connections']),
            (metrics['http.current_client_connections'] / metrics_min_max['http.current_client_connections']),
            (metrics['http.user_agent_current_connections_count'] / metrics_min_max['http.user_agent_current_connections_count']),
            (int(metrics['response_data_stream_count']) / metrics_min_max['response_data_stream_count']),
            (metrics['response_data_wan_bandwidth'] / metrics_min_max['response_data_wan_bandwidth']),
            (metrics['response_data_lan_bandwidth'] / metrics_min_max['response_data_lan_bandwidth']),
            (metrics['response_data_total_bandwidth'] / metrics_min_max['response_data_total_bandwidth']),
            (metrics['response_data_stream_count_direct_play'] / metrics_min_max['response_data_stream_count_direct_play'])
        ))

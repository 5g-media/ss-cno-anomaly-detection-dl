import ast
import statistics
import math

metric_list = {
    'bytes_sent_rate',
    'bytes_recv_rate',
    'packets_sent_rate',
    'packets_recv_rate',
    'hostdb.cache.total_hits_rate',
    'http.current_active_client_connections',
    'http.current_client_connections',
    'http.user_agent_current_connections_count',
    'response_data_stream_count',
    'response_data_wan_bandwidth',
    'response_data_lan_bandwidth',
    'response_data_total_bandwidth',
    'response_data_stream_count_direct_stream',
    'response_data_stream_count_transcode',
    'response_data_stream_count_direct_play'
}
metric_list_dict = {
    'bytes_sent_rate': [],
    'bytes_recv_rate': [],
    'packets_sent_rate': [],
    'packets_recv_rate': [],
    'hostdb.cache.total_hits_rate': [],
    'http.current_active_client_connections': [],
    'http.current_client_connections': [],
    'http.user_agent_current_connections_count': [],
    'response_data_stream_count': [],
    'response_data_wan_bandwidth': [],
    'response_data_lan_bandwidth': [],
    'response_data_total_bandwidth': [],
    'response_data_stream_count_direct_stream': [],
    'response_data_stream_count_transcode': [],
    'response_data_stream_count_direct_play': []
}

#with open('edge_regular.txt') as f:
#     for line in f:
#         metrics = ast.literal_eval(line)
#         for metric in metric_list:
#             metric_list_dict[metric].append(metrics[metric])
with open('all_mixed.txt') as f:
    for line in f:
        metrics = ast.literal_eval(line)
        for metric in metric_list:
            #if float(metrics[metric]) < 30:
            metric_list_dict[metric].append(metrics[metric])
for metric, values in metric_list_dict.items():
    print(metric, min(map(float, values)), max(map(float, values)), statistics.mean(map(float, values)))
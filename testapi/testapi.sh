curl -X 'POST' \
  'http://127.0.0.1:1145/chat/kb_chat' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
    "query": "综合评价总分的详细组成？",
    "mode": "local_kb",
    "kb_name": "campus",
    "top_k": 6,
    "score_threshold": 1.0,
    "history": [],
    "stream": true,
    "model": "custom-glm4-chat",
    "temperature": 0.7,
    "max_tokens": 0,
    "prompt_name": "default",
    "return_direct": false
    }'
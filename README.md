# AI Policy Insights

**AI Policy Insights** is an intelligent document processing system for answering natural language questions from uploaded insurance policies (PDFs, images, or plain text). It uses OCR and HuggingFace LLMs to provide insights with TTL-based auto-cleanup and metadata filtering.

---

## 🚀 Features

- 📄 Upload documents (PDF, images, or plain text)
- 🤖 Ask questions and get AI-generated answers
- 🔍 Full-text search with filters (source, tags, date)
- ⏳ Time-to-live (TTL) auto-expiry for cleanup
- 🔗 REST APIs for integration
- 🧪 MongoDB Atlas storage with indexing

---

## 🛠️ Tech Stack

- **Backend**: Spring Boot (Java 17)
- **Database**: MongoDB Atlas
- **OCR**: Tesseract
- **AI/LLM**: HuggingFace Inference API
- **Build Tool**: Gradle

---

## 📁 Folder Structure

    src/
    ├── main/
    │   ├── java/
    │   │   └── com/ai/policy/
    │   │       ├── controller/
    │   │       ├── service/
    │   │       ├── model/
    │   │       └── config/
    │   └── resources/
    │       ├── application.properties
    │       └── static/


---

## 📦 Setup Instructions

1. **Clone the repository**

```bash
git clone https://github.com/ayush-kumar774/ai-policy-insights.git
cd ai-policy-insights
```

2. **Create a `.env` file (or configure in `application.properties`) with:**
```properties
huggingface.api.token=your_token_here
huggingface.api.url=https://api-inference.huggingface.co/models/deepset/roberta-base-squad2
spring.data.mongodb.uri=mongodb://localhost:27017/ai-policy-insights-db
tesseract.data-path=/path/to/tessdata
policy.ttl.days=1
```

3. **Run the application using Gradle**
```bash
./gradlew bootRun
```

## 📮 API Endpoints

### `POST /api/v1/upload`
Upload a document (PDF, image, or text file)

---

### `POST /api/v1/ask`
Ask a natural language question about an uploaded document  
**Request Body Example:**

```json
{
  "docId": "64fe123abc",
  "question": "What is the sum insured in this policy?"
}
```

### `GET /api/v1/search`

Search uploaded documents using filters like `keyword`, `source`, or `tag`.

**Example Request:**
```json
/api/v1/search?keyword=fire&source=uploaded&tag=health
```

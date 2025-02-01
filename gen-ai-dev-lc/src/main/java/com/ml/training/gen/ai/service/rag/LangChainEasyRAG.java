package com.ml.training.gen.ai.service.rag;

import com.ml.training.gen.ai.service.rag.prompt.LangChainRAGAssistant;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.aggregator.DefaultContentAggregator;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.transformer.ExpandingQueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LangChainEasyRAG {

  private static final Logger LOG = LoggerFactory.getLogger(LangChainEasyRAG.class);

  public static void main(String[] args) {
    final String apiKey = System.getenv("AZURE_OPEN_AI_KEY");
    final String endpoint = System.getenv("AZURE_OPEN_AI_ENDPOINT");

    // load documents
    final Document document = FileSystemDocumentLoader.loadDocument(
        "some-path",
        new ApachePdfBoxDocumentParser()
    );

    final var splitter = DocumentSplitters.recursive(2000, 100);
    final var segments = splitter.split(document);

    // init embedding store
    final InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

    // init embedding model
    final EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

    // init ingestor & ingest data
    final EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
        // you could add additional metadata to each document
        //.documentTransformer()

        // splits docs to smaller chunks
        .documentSplitter(DocumentSplitters.recursive(1000, 100))

        // you could extend metadata for each TextSegment
        // .textSegmentTransformer()

        // you could set embedding model. in particular case is used standalone bge-small-en-v1.5
        .embeddingModel(embeddingModel)

        .embeddingStore(embeddingStore)

        .build();

    ingestor.ingest(document);

    final var chatModel = AzureOpenAiChatModel.builder()
        .apiKey(apiKey)
        .endpoint(endpoint)
        .deploymentName("gpt-4-0613")
        .logRequestsAndResponses(true)
        .build();

    // query transformer
    final ExpandingQueryTransformer queryTransformer = ExpandingQueryTransformer.builder()
        .chatLanguageModel(chatModel)
        .n(3)
        .build();

    // content retriever
    final ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
        .embeddingStore(embeddingStore)
        .embeddingModel(embeddingModel)
        .maxResults(5)
        .minScore(0.75)
        .build();

    // content aggregator
    final ContentAggregator contentAggregator = new DefaultContentAggregator();

    // content injector - inject contents into final prompt
    final ContentInjector contentInjector = DefaultContentInjector.builder()
        .build();

    final RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
        .queryTransformer(queryTransformer)
        .contentRetriever(contentRetriever)
        .contentAggregator(contentAggregator)
        .contentInjector(contentInjector)
        // to run all above simultaneously
        //.executor(executor)
        .build();

    final var assistant = AiServices.builder(LangChainRAGAssistant.class)
        .chatLanguageModel(chatModel)
        .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
        // simple way, naive RAG
        //.contentRetriever(contentRetriever)
        .retrievalAugmentor(retrievalAugmentor)
        .build();

    String answer = assistant.chat("How to do Easy RAG with LangChain4j?");
    System.out.println("Answer: " + answer);

    System.out.println("done");
  }

}

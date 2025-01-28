package com.ml.training.gen.ai.service.embedding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.embedding.mapping.TextEntityMapper;
import com.ml.training.gen.ai.service.embedding.model.TextVector;
import com.ml.training.gen.ai.service.embedding.repository.EmbeddingRepository;
import com.ml.training.gen.ai.service.embedding.repository.model.ScoredTextEntity;
import com.ml.training.gen.ai.service.embedding.repository.model.TextEntity;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmbeddingServiceImpl.class, TextEntityMapper.class})
class EmbeddingServiceImplTest {

  @MockBean
  private EmbeddingRepository repository;
  @MockBean
  private EmbeddingFunction embeddingFunction;

  @Autowired
  private EmbeddingServiceImpl service;

  @Test
  public void embedWorks() {
    final TextVector expected = TextVector.builder()
        .withText("text")
        .withVector(List.of(1f, 2f, 3f))
        .build();

    when(embeddingFunction.embed(eq(expected.getText()))).thenReturn(expected);

    final var actual = service.embed(expected.getText());

    assertSame(expected, actual);

    verify(embeddingFunction, times(1)).embed(eq(expected.getText()));
    verifyNoMoreInteractions(embeddingFunction, repository);
  }

  @Test
  public void scoreWorks() {
    final TextVector query = TextVector.builder()
        .withText("query")
        .withVector(List.of(10f, 10f, 10f))
        .build();

    final TextVector doc1 = TextVector.builder()
        .withText("doc1")
        .withVector(List.of(10.2f, 12.4f, 3.5f))
        .build();
    final TextVector doc2 = TextVector.builder()
        .withText("doc2")
        .withVector(List.of(15.2f, 45.4f, 90.5f))
        .build();

    when(embeddingFunction.getClientType()).thenReturn(ClientType.LC_OPEN_AI);
    when(embeddingFunction.embed(eq(query.getText()))).thenReturn(query);

    when(embeddingFunction.embed(eq(doc1.getText()))).thenReturn(doc1);
    when(embeddingFunction.embed(eq(doc2.getText()))).thenReturn(doc2);

    final var actual = service.score(query.getText(), List.of(doc1.getText(), doc2.getText()));

    assertEquals(2, actual.size());

    final var score1 = actual.get(0);
    assertEquals(doc1.getText(), score1.getText());
    assertTrue((score1.getScore() > 0.9f) && (score1.getScore() < 0.95f));

    final var score2 = actual.get(1);
    assertEquals(doc2.getText(), score2.getText());
    assertTrue((score2.getScore() > 0.8f) && (score2.getScore() < 0.9f));

    verify(embeddingFunction, atLeastOnce()).getClientType();

    verify(embeddingFunction, times(1)).embed(eq(query.getText()));

    verify(embeddingFunction, times(1)).embed(eq(doc1.getText()));
    verify(embeddingFunction, times(1)).embed(eq(doc2.getText()));

    verifyNoMoreInteractions(embeddingFunction, repository);
  }

  @Test
  public void saveWorks() {
    final TextVector expected = TextVector.builder()
        .withText("text")
        .withVector(List.of(1f, 2f, 3f))
        .build();

    final TextEntity entity = TextEntity.builder()
        .withId(UUID.randomUUID())
        .withText(expected.getText())
        .withVector(expected.getVector())
        .build();

    when(embeddingFunction.getClientType()).thenReturn(ClientType.LC_OPEN_AI);
    when(embeddingFunction.embed(eq(expected.getText()))).thenReturn(expected);

    when(repository.save(any(TextEntity.class))).thenReturn(entity);

    final var actual = service.save(expected.getText());

    assertEquals(entity.getId(), actual.getId());
    assertSame(expected.getText(), actual.getText());
    assertSame(expected.getVector(), actual.getVector());

    verify(embeddingFunction, times(1)).embed(eq(expected.getText()));
    verify(repository, times(1)).save(any(TextEntity.class));

    verifyNoMoreInteractions(embeddingFunction, repository);
  }

  @Test
  public void searchWorks() {
    final int limit = 10;
    final float score = 0.92f;
    final TextVector query = TextVector.builder()
        .withText("text")
        .withVector(List.of(1f, 2f, 3f))
        .build();

    final ScoredTextEntity entity = ScoredTextEntity.builder()
        .withText("doc1")
        .withScore(0.93f)
        .build();

    when(embeddingFunction.getClientType()).thenReturn(ClientType.LC_OPEN_AI);
    when(embeddingFunction.embed(eq(query.getText()))).thenReturn(query);

    when(repository.search(eq(query.getVector()), eq(limit), eq(score))).thenReturn(List.of(entity));

    final var actual = service.search(query.getText(), limit, score);

    assertEquals(1, actual.size());

    final var item1 = actual.get(0);
    assertEquals(entity.getText(), item1.getText());
    assertEquals(entity.getScore(), item1.getScore().floatValue());

    verify(embeddingFunction, atLeastOnce()).getClientType();
    verify(embeddingFunction, times(1)).embed(eq(query.getText()));
    verify(repository, times(1)).search(
        eq(query.getVector()), eq(limit), eq(score)
    );

    verifyNoMoreInteractions(embeddingFunction, repository);
  }

}
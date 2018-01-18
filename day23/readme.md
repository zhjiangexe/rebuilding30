# Spring MVC Controller Unit Test《三》

接續上一篇

本篇為了使用@Valid及@NotBlank，請先引入套件
```java
compile group: 'org.hibernate', name: 'hibernate-validator', version: '5.2.5.Final'
```

## 待測系統介紹
相同的IssueController，這裡要進行驗證的是：
- POST /api/issue - 傳入正確的RequestBody，並驗證回傳的資訊
    1. 新增Issue發現title未填驗證失敗。如果經過@Valid發現有屬性未通過驗證(本案例為title必填)，則回傳http status code 400，並產生錯誤訊息。
    2. 新增Issue成功。成功新增issue則回傳建立的issue訊息，以及http status code 201
```java
@RestController
@RequestMapping("/api/issue")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Issue create(@Valid @RequestBody Issue issue) {
        return issueService.create(issue);
    }
}
```

Issue類別，加入了可以跟@Valid配合的@NotBlank，這裡如果驗證未通過，則會拋出MethodArgumentNotValidException。
```
package com.rebuilding.day.entity;

import org.hibernate.validator.constraints.NotBlank;

public class Issue {
    private Long id; // issue單號

    @NotBlank
    private String title; // 標題

    private String desc; // 內容描述
    private Creator creator; // 建立人
    private Assignee assignee; // 負責人
    private Closer closer; // 結案人
    private State state; // 狀態TODO, IN_PROGRESS, DONE
    private Result result; // 結案形式RESOLVED, REJECT
```

輸出輸入Controller層會經過設定好的`@ControllerAdvice`，並註解@ExceptionHandler指定處理MethodArgumentNotValidException，加工後回傳相應的資訊。

```java
package com.rebuilding.day.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class ErrorHandleAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandleAdvice.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorVo processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return processFieldErrors(fieldErrors);
    }

    private ValidationErrorVo processFieldErrors(List<FieldError> fieldErrors) {
        ValidationErrorVo dto = new ValidationErrorVo();
        for (FieldError fieldError : fieldErrors) {
            String localizedErrorMessage = resolveErrorCode(fieldError);
            dto.addFieldError(fieldError.getField(), localizedErrorMessage);
        }
        return dto;
    }

    private String resolveErrorCode(FieldError fieldError) {
        String[] fieldErrorCodes = fieldError.getCodes();
        return fieldErrorCodes[fieldErrorCodes.length - 1];
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void returnHttpStatusCodeNotFound() {
        LOGGER.error("Something was found not. Returning HTTP status code 404");
    }
}

/**
* 加工過程中使用到的物件，填入發生錯誤的屬性及其原因
**/
public class FieldErrorVo {

    private final String field;
    private final String errorCode;

    public FieldErrorVo(String field, String errorCode) {
        this.field = field;
        this.errorCode = errorCode;
    }

    public String getField() {
        return field;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

/**
* 加工過程中使用到的物件，收集所有發生錯誤的屬性
**/
public class ValidationErrorVo {

    private final List<FieldErrorVo> fieldErrors = new ArrayList<>();

    public ValidationErrorVo() {
    }

    public void addFieldError(String path, String message) {
        FieldErrorVo error = new FieldErrorVo(path, message);
        fieldErrors.add(error);
    }

    public List<FieldErrorVo> getFieldErrors() {
        return fieldErrors;
    }
}
```

title未填驗證不過，回傳的錯誤資訊
```json
{
    "fieldErrors":[
        {
            "field":"title",
            "errorCode":"NotBlank"
        }
    ]
}
```

Controller單元測試的配置，請參考以下程式碼：
```java
package com.rebuilding.day.controller;

import com.rebuilding.day.config.TestContextConfig;
import com.rebuilding.day.service.IssueService;
import com.rebuilding.day.web.ErrorHandleAdvice;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.mock;

@RunWith(HierarchicalContextRunner.class)
public class IssueControllerTest {

    private IssueService issueService;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        issueService = mock(IssueService.class);
        IssueController issueController = new IssueController();
        ReflectionTestUtils.setField(issueController, "issueService", issueService);

        mockMvc = MockMvcBuilders.standaloneSetup(issueController)
                /**
                * 額外加入了ControllerAdvice
                **/
                .setControllerAdvice(new ErrorHandleAdvice())
                .setMessageConverters(TestContextConfig.objectMapperHttpMessageConverter())
                .build();
    }
}
```

因為測試案例中會須需要用到json格式的資料，這裡建立一個工具類別來傳入物件後回傳json格式的資訊
```java
package com.rebuilding.day.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static com.rebuilding.day.config.TestContextConfig.objectMapper;

public final class WebTestUtil {

    private WebTestUtil() {
    }

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = objectMapper();
        return mapper.writeValueAsBytes(object);
    }
}
```

## <情境一>新增Issue發現title未填驗證失敗
1. 驗證http status code為400。
2. 驗證它回傳一個錯誤資訊。
3. 驗證它是回傳正確的錯誤資訊。
4. 驗證它沒有建立一個錯誤的issue。

這邊直接列出第二層類別，請參考以下程式碼：
```java
public class Create {
    Issue input;

    public class WhenFieldsAreEmpty {
        /**
        * 依照我們的測試案例，建立一個title及desc為空的Issue物件
        **/
        @Before
        public void createEmptyInput() {
            input = new Issue();
            input.setTitle("");
            input.setDesc("");
        }

        /**
        * 1. 驗證HTTP status code為400。
        **/
        @Test
        public void thenReturnHttpStatusCodeBadRequest() throws Exception {
            /**
            * 使用mockMvc發動一個request POST /api/issue，
            * 並將指定物件轉成json格式傳送
            **/
            mockMvc.perform(post("/api/issue")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(convertObjectToJsonBytes(input))
            ).andExpect(status().isBadRequest());
        }

        /**
        * 2. 驗證它回傳一個錯誤資訊。
        **/
        @Test
        public void thenReturnOneValidationError() throws Exception {
            mockMvc.perform(post("/api/issue")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(convertObjectToJsonBytes(input))
            )
                    .andExpect(jsonPath("$.fieldErrors", hasSize(1)));
        }

        /**
        * 3. 驗證它是回傳正確的錯誤資訊。
        **/
        @Test
        public void thenReturnValidationErrorAboutEmptyTitle() throws Exception {
            ResultActions perform = mockMvc.perform(post("/api/issue")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(convertObjectToJsonBytes(input))
            );
            perform
                    .andExpect(
                            jsonPath("$.fieldErrors[0].field", is("title"))
                    )
                    .andExpect(
                            jsonPath("$.fieldErrors[0].errorCode", is("NotBlank"))
                    );
        }

        /**
        * 4. 驗證它沒有建立一個錯誤的issue。
        * 指的是IssueController沒有調用相依物件issueService的方法create
        **/
        @Test
        public void thenNotCreateIssue() throws Exception {
            mockMvc.perform(post("/api/issue")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(WebTestUtil.convertObjectToJsonBytes(input))
            );

            verify(issueService, never()).create(isA(Issue.class));
        }
    }
}
```

## <情境二>新增Issue成功

1. 驗證回傳http status code為201。
2. 驗證它回傳正確的資訊。
3. 驗證它建立一個空id的issue。
4. 驗證它使用正確的標題建立了一個任務。
5. 驗證它使用正確的描述建立了一個任務。

```java
public class Create {
    Issue input;
    public class WhenAllFieldsAreValid {
        @Before
        public void setup() {
            createValidInput();
            returnCreated();
        }

        private void createValidInput() {
            input = new Issue();
            input.setTitle("inputTitle");
            input.setDesc("inputDesc");
        }

        private void returnCreated() {
            Issue created = new Issue();
            created.setId(1L);
            created.setTitle("respTitle");
            created.setDesc("respDesc");
            created.setState(State.TODO);
            created.setCreator(new Creator(100L));
            given(issueService.create(isA(Issue.class))).willReturn(created);
        }

        /**
        * 1. 驗證回傳http status code為201。
        **/
        @Test
        public void thenReturnHttpStatusCodeCreated() throws Exception {
            mockMvc.perform(post("/api/issue")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(WebTestUtil.convertObjectToJsonBytes(input)))
                    .andExpect(status().isCreated());
        }

        /**
        * 2. 驗證它回傳正確的資訊。
        **/
        @Test
        public void thenReturnCorrectInfo() throws Exception {
            mockMvc.perform(post("/api/issue")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(WebTestUtil.convertObjectToJsonBytes(input)))
                    .andExpect(jsonPath("$.creator.memberNum", is(100)))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.title", is("respTitle")))
                    .andExpect(jsonPath("$.desc", is("respDesc")))
                    .andExpect(jsonPath("$.state", is(State.TODO.toString())));
        }

        /**
        * 3. 驗證它建立一個空id的issue。
        **/
        @Test
        public void thenCreateIssueWithoutId() throws Exception {
            mockMvc.perform(post("/api/issue")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(WebTestUtil.convertObjectToJsonBytes(input)));

            verify(issueService, times(1))
                    .create(
                            assertArg(issue -> assertThat(issue.getId()).isNull())
                    );
        }

        /**
        * 4. 驗證它使用正確的標題建立了一個任務。
        **/
        @Test
        public void thenCreateIssueWithCorrectTitle() throws Exception {
            mockMvc.perform(post("/api/issue")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(WebTestUtil.convertObjectToJsonBytes(input)));

            verify(issueService, times(1))
                    .create(
                            assertArg(issue -> assertThat(issue.getTitle()).isEqualTo("inputTitle"))
                    );
        }

        /**
        * 5. 驗證它使用正確的描述建立了一個任務。
        **/
        @Test
        public void thenCreateIssueWithCorrectDesc() throws Exception {
            mockMvc.perform(post("/api/issue")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(WebTestUtil.convertObjectToJsonBytes(input)));

            verify(issueService, times(1))
                    .create(
                            assertArg(issue -> assertThat(issue.getDesc()).isEqualTo("inputDesc"))
                    );
        }
    }
}
```

以上，Spring MVC Controller的單元測試就到這邊囉。

下一篇要往整合測試邁進～
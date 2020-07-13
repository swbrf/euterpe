package euterpe.mybatis.mapper;

import euterpe.po.WebTextKnowledge;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
//@MybatisTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class WebTextKnowledgeMapperTest {

    @Autowired
    private WebTextKnowledgeMapper mapper;

    @BeforeClass
    public static void init() {
        System.setProperty("spring.profiles.active", "test");
    }

    @Test
    public void testSelectList() {
        List<WebTextKnowledge> userList = mapper.selectList(null);
        userList.forEach(System.out::println);
    }

    @Test
    public void testInsert() {
        WebTextKnowledge info = new WebTextKnowledge();
        info.setTitle("标题");
        info.setContent("正文");
        info.setArticleTypeTags("标签");
        info.setOriginalAuthor("作者");
        info.setSourceUrl("url");
        info.setDownloadTime(Timestamp.valueOf(LocalDateTime.now()));

        System.out.println(mapper.insert(info));
    }

}
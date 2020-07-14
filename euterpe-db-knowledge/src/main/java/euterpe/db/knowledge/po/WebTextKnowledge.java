package euterpe.db.knowledge.po;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author ryan.wang
 */
@Data
public class WebTextKnowledge implements Serializable {

    private Long id;

    private String title;

    private String content;

    private String articleTypeTags;

    private String originalAuthor;

    private String sourceUrl;

    private Timestamp downloadTime;

    private Timestamp createTime;

    private Timestamp updateTime;

    private Byte isDeleted;

}

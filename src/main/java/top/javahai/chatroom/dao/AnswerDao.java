package top.javahai.chatroom.dao;

import top.javahai.chatroom.entity.Answer;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface AnswerDao {

    /**
     * 新增数据
     *
     * @param answer 实例对象
     * @return 影响行数
     */
    int insert(Answer answer);

    /**
     * 修改数据valid
     *
     * @param answer 实例对象
     * @return 影响行数
     */
    int updateValid(Integer id,Boolean valid);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Answer queryById(Integer id);
    
    /**
     * 查询所有数据
     *
     * @return 对象列表
     */
    List<Answer> queryAll();

    /**
     * 通过userId查询数据
     *
     * @param userId 用户ID
     * @return 对象列表
     */
    List<Answer> queryByUserId(Integer userId);

    /**
     * 通过questionId查询数据
     *
     * @param questionId 问题ID
     * @return 对象列表
     */
    List<Answer> queryByQuestionId(Integer questionId);
}

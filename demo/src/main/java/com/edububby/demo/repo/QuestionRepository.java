package com.edububby.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.edububby.demo.dto.ProblemSolvedDTO;
import com.edububby.demo.dto.ProblemUploadDTO;
import com.edububby.demo.model.QuestionBank;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionBank, Long> {

  

    @Query(value = """
            SELECT
                qb.qes_type AS qesType,
                COUNT(DISTINCT s.qes_idx) AS questionCount,
                GROUP_CONCAT(DISTINCT s.qes_idx) AS qesIdxList
            FROM
                tb_solving s
            JOIN
                tb_question_bank qb
            ON
                s.qes_idx = qb.qes_idx
            WHERE
                s.user_id = :userId
                AND (s.corr_answer_yn = 'N' OR s.solving_fav = 'Y')
            GROUP BY
                qb.qes_type
            LIMIT 0, 1000
            """, nativeQuery = true)
    public List<Object[]> findQuestionCountWithQesIdxByUserId(String userId);



    @Query("SELECT new com.edububby.demo.dto.ProblemSolvedDTO(" +
    "qb.qesIdx, qb.qesType, qb.qesContent, qb.qesAnswer, qb.qesDt, qb.qesLevel, " +
    "qb.qesSel1, qb.qesSel2, qb.qesSel3, qb.qesSel4, qb.qesSel5, qb.qesExp, qb.qesImg1, qb.qesImg2, " +
    "s.wrongCnt, s.solvingFav, " +
    "qk.keyword1, qk.keyword2, qk.keyword3, qk.keyword4, qk.keyword5) " +
    "FROM QuestionBank qb " +
    "LEFT JOIN Solving s ON qb.qesIdx = s.qesIdx AND s.userId = :userId " +
    "LEFT JOIN QuestionKeyword qk ON qb.qesIdx = qk.qesIdx " +
    "WHERE qb.qesIdx IN :qesIdxList")
    public List<ProblemSolvedDTO> ProblemSolved(@Param("qesIdxList") List<Long> qesIdxList,@Param("userId") String userId);
    
    
    @Query("""
        SELECT new com.edububby.demo.dto.ProblemSolvedDTO(
            qb.qesIdx, qb.qesType, qb.qesContent, qb.qesAnswer, qb.qesDt, qb.qesLevel,
            qb.qesSel1, qb.qesSel2, qb.qesSel3, qb.qesSel4, qb.qesSel5, qb.qesExp, qb.qesImg1, qb.qesImg2,
            s.wrongCnt, s.solvingFav,
            qk.keyword1, qk.keyword2, qk.keyword3, qk.keyword4, qk.keyword5
        )
        FROM QuestionBank qb
        JOIN Solving s ON qb.qesIdx = s.qesIdx
        LEFT JOIN QuestionKeyword qk ON qb.qesIdx = qk.qesIdx
        WHERE s.userId = :userId
          AND (s.corrAnswerYn = 'N' OR s.solvingFav = 'Y')
        """)
    List<ProblemSolvedDTO> findProblemsByUserId(@Param("userId") String userId);
    
    

    

    @Query("SELECT new com.edububby.demo.dto.ProblemUploadDTO(" + 
    "qb.qesIdx, qb.qesType, qb.qesContent, qb.qesAnswer, qb.qesDt, qb.qesLevel, " +
    "qb.qesSel1, qb.qesSel2, qb.qesSel3, qb.qesSel4, qb.qesSel5, qb.qesExp, qb.qesImg1, qb.qesImg2, " +
    "qk.keyword1, qk.keyword2, qk.keyword3, qk.keyword4, qk.keyword5) " +
    "FROM QuestionBank qb " +
    "LEFT JOIN QuestionKeyword qk ON qb.qesIdx = qk.qesIdx " +
    "WHERE qb.qesIdx IN :qesIdx")
    List<ProblemUploadDTO> findQuestionByQesIdxIn(List<Long> qesIdx);


}

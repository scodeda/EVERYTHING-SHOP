package study.toy.everythingshop.repository.impl;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;
import study.toy.everythingshop.dto.DiscountPolicyDTO;
import study.toy.everythingshop.dto.PointHistoryDTO;
import study.toy.everythingshop.dto.SalesSummaryDTO;
import study.toy.everythingshop.dto.SellerApplyDTO;
import study.toy.everythingshop.logTrace.Trace;
import study.toy.everythingshop.repository.MyPageDAO;

import java.util.List;

/**
 * fileName : MyPageDAOImpl
 * author   : pilming
 * date     : 2023-03-08
 */
@Repository
@RequiredArgsConstructor
@Trace
public class MyPageDAOImpl implements MyPageDAO {
    private final SqlSession sqlSession;

    @Override
    public int insertSellerApply(int userNum) {
        return sqlSession.insert("maria.MyPageDAO.insertSellerApply", userNum);
    }

    @Override
    public List<SellerApplyDTO> selectSellerApply(SellerApplyDTO sellerApplyDTO) {
        return sqlSession.selectList("maria.MyPageDAO.selectSellerApply", sellerApplyDTO);
    }

    @Override
    public int selectSellerApplyTotalCount(int userNum) {
        return sqlSession.selectOne("maria.MyPageDAO.selectSellerApplyTotalCount", userNum);
    }

    @Override
    public int selectApplyCount(int userNum) {
        return sqlSession.selectOne("maria.MyPageDAO.selectApplyCount", userNum);
    }

    @Override
    public List<DiscountPolicyDTO> selectDiscountPolicy() {
        return sqlSession.selectList("maria.MyPageDAO.selectDiscountPolicy");
    }

    @Override
    public int updateDiscountPolicyEndDate(DiscountPolicyDTO discountPolicyDTO) {
        return sqlSession.update("maria.MyPageDAO.updateDiscountPolicyEndDate", discountPolicyDTO);
    }

    @Override
    public int insertDiscountPolicy(DiscountPolicyDTO discountPolicyDTO) {
        return sqlSession.insert("maria.MyPageDAO.insertDiscountPolicy", discountPolicyDTO);
    }

    @Override
    public List<PointHistoryDTO> selectPointHistory(PointHistoryDTO pointHistoryDTO) {
        return sqlSession.selectList("maria.MyPageDAO.selectPointHistory", pointHistoryDTO);
    }

    @Override
    public int selectPointHistoryTotalCount(PointHistoryDTO pointHistoryDTO) {
        return sqlSession.selectOne("maria.MyPageDAO.selectPointHistoryTotalCount", pointHistoryDTO);
    }

    @Override
    public List<SalesSummaryDTO> selectSalesSummary(SalesSummaryDTO salesSummaryDTO) {
        return sqlSession.selectList("maria.MyPageDAO.selectSalesSummary", salesSummaryDTO);
    }

    @Override
    public int selectSalesSummaryTotalCount(SalesSummaryDTO salesSummaryDTO) {
        return sqlSession.selectOne("maria.MyPageDAO.selectSalesSummaryTotalCount", salesSummaryDTO);
    }

    @Override
    public int selectTotalSalesPrice() {
        return sqlSession.selectOne("maria.MyPageDAO.selectTotalSalesPrice");
    }
}

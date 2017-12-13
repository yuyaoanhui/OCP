import org.springframework.util.StringUtils;

import com.futuredata.judicature.base.exception.FdBizException;
import com.futuredata.judicature.base.model.BaseModel;
import com.futuredata.judicature.base.result.BaseResultCode;

public class test {
  void function() throws FdBizException {
    BaseModel model = new BaseModel();
    System.out.println(model.toString());
    // throw new FdBizException("123");
    if (StringUtils.isEmpty(model.getId())) {
      throw new FdBizException("123", BaseResultCode.SYS_SUCCESS, new Object[] {model});
    }
    // throw new FdBizException("123", BaseResultCode.SYS_SUCCESS);
  }

  public static void main(String[] args) throws FdBizException {
    test a = new test();
    a.function();
  }
}

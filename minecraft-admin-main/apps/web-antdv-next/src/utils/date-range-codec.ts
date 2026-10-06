import type { Dayjs } from 'dayjs';

import dayjs from 'dayjs';

interface DateRangeCodecOptions {
  /** 日期格式化格式 */
  dateFormat?: string;
  /** 编码后的结束日期字段名 */
  endField: string;
  /** 表单中的日期范围字段名 */
  rangeField: string;
  /** 编码后的开始日期字段名 */
  startField: string;
}

/**
 * 日期范围编解码器：
 * encode 将表单中的 [Dayjs, Dayjs] 范围字段拆分为 start/end 两个字符串字段（用于提交），
 * decode 将 start/end 两个字段合并回范围字段（用于回显）。
 */
export function createDateRangeCodec<TForm extends Record<string, any>>() {
  return function (
    options: DateRangeCodecOptions,
  ): {
    decode: (values: Record<string, any>) => TForm;
    encode: (values: TForm) => Record<string, any>;
  } {
    const { dateFormat = 'YYYY-MM-DD', endField, rangeField, startField } = options;

    function encode(values: TForm): Record<string, any> {
      const { [rangeField]: range, ...rest } = values;
      if (Array.isArray(range) && range.length === 2 && range[0] && range[1]) {
        return {
          ...rest,
          [endField]: dayjs(range[1] as Dayjs).format(dateFormat),
          [startField]: dayjs(range[0] as Dayjs).format(dateFormat),
        };
      }
      return rest;
    }

    function decode(values: Record<string, any>): TForm {
      const start = values?.[startField];
      const end = values?.[endField];
      if (!start || !end) {
        return { ...values } as TForm;
      }
      const { [startField]: _start, [endField]: _end, ...rest } = values;
      return { ...rest, [rangeField]: [dayjs(start), dayjs(end)] } as unknown as TForm;
    }

    return { decode, encode };
  };
}

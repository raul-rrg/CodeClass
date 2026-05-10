import { IconJS, IconPython, IconJava } from '@/components/icons/LangIcons.js'

export const LANGUAGES = [
    {
        id:          'javascript',
        label:       'JavaScript',
        icon:        IconJS,
        activeClass: 'bg-yellow-400/8 text-yellow-300/80 ring-1 ring-yellow-400/20',
        starter:     `function twoSum(nums, target) {\n    // Tu solución aquí\n}\n`,
    },
    {
        id:          'python',
        label:       'Python',
        icon:        IconPython,
        activeClass: 'bg-green-500/10 text-green-300 ring-1 ring-green-500/30',
        starter:     `def two_sum(nums, target):\n    # Tu solución aquí\n    pass\n`,
    },
    {
        id:          'java',
        label:       'Java',
        icon:        IconJava,
        activeClass: 'bg-orange-500/10 text-orange-300 ring-1 ring-orange-400/30',
        starter:     `class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        // Tu solución aquí\n        return new int[]{};\n    }\n}\n`,
    },
]
